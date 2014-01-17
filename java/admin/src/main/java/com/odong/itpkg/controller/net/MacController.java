package com.odong.itpkg.controller.net;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Mac;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.entity.uc.User;
import com.odong.itpkg.form.net.MacForm;
import com.odong.itpkg.linux.ArchHelper;
import com.odong.itpkg.model.Rpc;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.rpc.RpcHelper;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.service.LogService;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.Form;
import com.odong.portal.web.form.RadioField;
import com.odong.portal.web.form.SelectField;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午11:28
 */
@Controller("c.net.mac")
@RequestMapping(value = "/net/mac/{hostId}")
@SessionAttributes(SessionItem.KEY)
public class MacController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getIndex(@PathVariable long hostId, Map<String, Object> map) {
        Host host = hostService.getHost(hostId);
        List<Mac> macList = hostService.listMacByHost(host.getId());
        Map<Long, User> userMap = new HashMap<>();
        for (Mac m : macList) {
            if (m.getUser() != null) {
                userMap.put(m.getId(), accountService.getUser(m.getUser()));
            }
        }
        map.put("macList", macList);
        map.put("userMap", userMap);
        map.put("host", hostService.getHost(hostId));
        return "net/mac";
    }

    @RequestMapping(value = "/{macId}", method = RequestMethod.GET)
    @ResponseBody
    Form getMac(@PathVariable long hostId, @PathVariable long macId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("mac", "编辑MAC[" + macId + "]", "/net/mac/" + hostId + "/" + macId);
        Mac mac = hostService.getMac(macId);
        List<User> userList = accountService.listUserByCompany(si.getSsCompanyId());
        if (userList.size() > 0) {
            if (mac != null && mac.getHost() == hostId) {
                SelectField<Long> user = new SelectField<>("user", "用户", mac.getUser());
                for (User u : userList) {
                    user.addOption(u.getUsername(), u.getId());
                }
                fm.addField(user);
                RadioField<Mac.State> state = new RadioField<Mac.State>("state", "状态", mac.getState());
                state.addOption("提交", Mac.State.SUBMIT);
                state.addOption("启用", Mac.State.ENABLE);
                state.addOption("禁用", Mac.State.DISABLE);
                fm.addField(state);
                fm.setOk(true);
            } else {
                fm.addData("MAC[" + macId + "]不存在");
            }
        } else {
            fm.addData("用户列表为空");
        }

        return fm;
    }

    @RequestMapping(value = "/{macId}", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postMac(@Valid MacForm form, BindingResult result, @PathVariable long hostId, @PathVariable long macId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            User u = accountService.getUser(form.getUser());
            Mac m = hostService.getMac(macId);
            if (u != null && m != null && m.getHost() == hostId && u.getCompany().equals(si.getSsCompanyId())) {
                hostService.setMacState(macId, form.getState());
                hostService.setMacUser(macId, form.getUser());
                logService.add(si.getSsAccountId(), "修改MAC[" + macId + "]：用户" + u.getUsername() + "，状态" + form.getState(), Log.Type.INFO);

            } else {
                ri.setOk(false);
                ri.addData("MAC[" + macId + "]不存在");
            }
        }
        return ri;
    }

    @RequestMapping(value = "/{macId}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseItem deleteMac(@PathVariable long hostId, @PathVariable long macId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        Mac mac = hostService.getMac(macId);
        if (mac != null && mac.getHost() == hostId) {
            hostService.delMac(macId);
            logService.add(si.getSsAccountId(), "删除MAC[" + macId + "]", Log.Type.INFO);
            ri.setOk(true);
        } else {
            ri.addData("MAC[" + macId + "]不存在");
        }
        return ri;
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postTest(@PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        Host h = hostService.getHost(hostId);
        for (int i = 100; i < 201; i++) {
            hostService.addMac(hostId, "ff:ff:ff:ff:ff:" + Integer.toHexString(i), i, h.getDefFlowLimit());
        }
        logService.add(si.getSsAccountId(), "填充主机[" + hostId + "]测试数据", Log.Type.INFO);
        ri.setOk(true);
        return ri;
    }

    @RequestMapping(value = "/scan", method = RequestMethod.GET)
    @ResponseBody
    ResponseItem getScan(@PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        Host host = hostService.getHost(hostId);
        try {
            Rpc.Response response = rpcHelper.command(hostId, archHelper.macScan());
            if (response.getCode() == Rpc.Code.SUCCESS) {
                List<String> list = response.getLinesList();
                ri.addData("共扫描到[" + (list.size() - 1) + "]记录");
                for (int i = 1; i < list.size(); i++) {
                    String[] ss = list.get(i).split("\\s+");
                    if (ss.length == 5) {
                        String ip = ss[0];
                        String serial = ss[2];
                        Mac mac = hostService.getMac(hostId, serial);
                        if (mac == null) {
                            ri.addData("添加记录[" + serial + "," + ip + "]");
                            hostService.addMac(hostId, serial, Integer.parseInt(ip.split("\\.")[3]), host.getDefFlowLimit());
                        }
                    }
                }
                logService.add(si.getSsAccountId(), "扫描主机[" + hostId + "]MAC列表", Log.Type.INFO);
                ri.setOk(true);
            } else {
                for (String s : response.getLinesList()) {
                    ri.addData(s);
                }
            }
        } catch (Exception e) {
            ri.addData(e.getMessage());
        }
        return ri;
    }

    @Resource
    private ArchHelper archHelper;
    @Resource
    private HostService hostService;
    @Resource
    private LogService logService;
    @Resource
    private RpcHelper rpcHelper;
    @Resource
    private FormHelper formHelper;
    @Resource
    private AccountService accountService;
    @Resource
    private EncryptHelper encryptHelper;

    public void setRpcHelper(RpcHelper rpcHelper) {
        this.rpcHelper = rpcHelper;
    }

    public void setArchHelper(ArchHelper archHelper) {
        this.archHelper = archHelper;
    }

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
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
