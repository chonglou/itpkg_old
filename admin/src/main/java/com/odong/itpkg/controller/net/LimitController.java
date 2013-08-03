package com.odong.itpkg.controller.net;

import com.odong.itpkg.entity.net.Mac;
import com.odong.itpkg.entity.net.firewall.FlowLimit;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.entity.uc.User;
import com.odong.itpkg.form.net.limit.BindForm;
import com.odong.itpkg.linux.ArchHelper;
import com.odong.itpkg.linux.EtcFile;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.rpc.RpcHelper;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.service.LogService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.Form;
import com.odong.portal.web.form.HiddenField;
import com.odong.portal.web.form.SelectField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 下午1:24
 */
@Controller("c.net.limit")
@RequestMapping(value = "/net/limit/{hostId}")
@SessionAttributes(SessionItem.KEY)
public class LimitController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getHost(@PathVariable long hostId, Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        List<Mac> macList = new ArrayList<>();
        Map<Long, FlowLimit> flowLimitMap = new HashMap<>();
        Map<Long, User> userMap = new HashMap<>();
        for (Mac m : hostService.listMacByHost(hostId)) {
            if (m.getState() == Mac.State.ENABLE) {
                macList.add(m);
                if (flowLimitMap.get(m.getFlowLimit()) == null) {
                    flowLimitMap.put(m.getFlowLimit(), hostService.getFirewallFlowLimit(m.getFlowLimit()));
                }
                if (userMap.get(m.getUser()) == null) {
                    userMap.put(m.getUser(), accountService.getUser(m.getUser()));

                }
            }
        }
        List<String> logs = new ArrayList<>();
        try {
            logs.addAll(rpcHelper.command(hostId, archHelper.tcStatus().toArray(new String[1])).getLinesList());
        } catch (Exception e) {
            logs.add(e.getMessage());
        }
        map.put("logs", logs);
        map.put("macList", macList);
        map.put("flowLimitMap", flowLimitMap);
        map.put("userMap", userMap);
        map.put("host", hostService.getHost(hostId));
        return "net/limit";
    }

    @RequestMapping(value = "/bind/{macId}", method = RequestMethod.GET)
    @ResponseBody
    Form getBindForm(@PathVariable long hostId, @PathVariable long macId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("limit", "修改MAC[" + macId + "]的限速规则", "/net/limit/" + hostId + "/bind");
        Mac m = hostService.getMac(macId);
        if (m != null && m.getHost() == hostId) {
            fm.addField(new HiddenField<Long>("mac", m.getId()));
            SelectField<Long> limit = new SelectField<Long>("flLimit", "限速规则", m.getFlowLimit());
            for (FlowLimit fl : hostService.listFirewallFlowLimit(si.getSsCompanyId())) {
                limit.addOption(fl.getName(), fl.getId());
            }
            fm.addField(limit);
            fm.setOk(true);
        } else {
            fm.addData("MAC[" + macId + "]不存在");
        }
        return fm;
    }

    @RequestMapping(value = "/bind", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postBindForm(@Valid BindForm form, BindingResult result, @PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            Mac m = hostService.getMac(form.getMac());
            FlowLimit fl = hostService.getFirewallFlowLimit(form.getFlLimit());
            if (m != null && fl != null && m.getHost() == hostId && fl.getCompany().equals(si.getSsCompanyId())) {
                hostService.setMacLimit(form.getMac(), form.getFlLimit());
                logService.add(si.getSsAccountId(), "变更MAC[" + form.getMac() + "]限速规则 => " + form.getFlLimit(), Log.Type.INFO);
            } else {
                ri.setOk(false);
                ri.addData("限速规则[" + form.getFlLimit() + "]不存在");
            }
        }
        return ri;
    }


    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem save(@PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        try {
            EtcFile ef = archHelper.tcProfile(hostId);
            rpcHelper.file(hostId, ef.getName(), ef.getOwner(), ef.getMode(), ef.getData());
            ri.setOk(true);
            logService.add(si.getSsAccountId(), "保存主机[" + hostId + "]防火墙规则", Log.Type.INFO);
        } catch (Exception e) {
            ri.addData(e.getMessage());
        }
        return ri;
    }

    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem clear(@PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        try {
            rpcHelper.command(hostId, archHelper.tcClear().toArray(new String[1]));
            ri.setOk(true);
            logService.add(si.getSsAccountId(), "清空主机[" + hostId + "]防火墙规则", Log.Type.INFO);
        } catch (Exception e) {
            ri.addData(e.getMessage());
        }
        return ri;
    }

    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem apply(@PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        try {
            rpcHelper.command(hostId, archHelper.tcApply(hostId).toArray(new String[1]));
            ri.setOk(true);
            logService.add(si.getSsAccountId(), "应用主机[" + hostId + "]限速规则", Log.Type.INFO);
        } catch (Exception e) {
            ri.addData(e.getMessage());
        }
        return ri;
    }

    @Resource
    private HostService hostService;
    @Resource
    private LogService logService;
    @Resource
    private AccountService accountService;
    @Resource
    private FormHelper formHelper;
    @Resource
    private ArchHelper archHelper;
    @Resource
    private RpcHelper rpcHelper;
    private final static Logger logger = LoggerFactory.getLogger(LimitController.class);

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setArchHelper(ArchHelper archHelper) {
        this.archHelper = archHelper;
    }

    public void setRpcHelper(RpcHelper rpcHelper) {
        this.rpcHelper = rpcHelper;
    }

    public void setHostService(HostService hostService) {
        this.hostService = hostService;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setFormHelper(FormHelper formHelper) {
        this.formHelper = formHelper;
    }

}
