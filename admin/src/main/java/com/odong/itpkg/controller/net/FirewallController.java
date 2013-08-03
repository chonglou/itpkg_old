package com.odong.itpkg.controller.net;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Mac;
import com.odong.itpkg.entity.net.firewall.*;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.form.net.firewall.InputAddForm;
import com.odong.itpkg.form.net.firewall.MacOutputForm;
import com.odong.itpkg.form.net.firewall.NatForm;
import com.odong.itpkg.form.net.firewall.OutputForm;
import com.odong.itpkg.linux.ArchHelper;
import com.odong.itpkg.linux.EtcFile;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.rpc.RpcHelper;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.service.LogService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.*;
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
 * Date: 13-7-18
 * Time: 上午11:27
 */
@Controller("c.net.firewall")
@RequestMapping(value = "/net/firewall/{hostId}")
@SessionAttributes(SessionItem.KEY)
public class FirewallController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getIndex(@PathVariable long hostId, Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Map<Long, DateLimit> dateLimitMap = new HashMap<>();
        for (DateLimit dl : hostService.listFirewallDateLimit(si.getSsCompanyId())) {
            dateLimitMap.put(dl.getId(), dl);
        }
        List<String> logs = new ArrayList<>();
        try {
            logs.addAll(rpcHelper.command(hostId, archHelper.ffStatus().toArray(new String[1])).getLinesList());
        } catch (Exception e) {
            logs.add(e.getMessage());
        }
        map.put("logs", logs);
        map.put("dateLimitMap", dateLimitMap);
        map.put("outList", hostService.listFirewallOutputByHost(hostId));
        map.put("inList", hostService.listFirewallInput(hostId));
        map.put("natList", hostService.listFirewallNat(hostId));
        map.put("host", hostService.getHost(hostId));
        return "net/firewall";
    }

    @RequestMapping(value = "/nat", method = RequestMethod.GET)
    @ResponseBody
    Form getNatAdd(@PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("firewall", "新增NAT规则", "/net/firewall/" + hostId + "/nat");
        fm.addField(new HiddenField<Long>("id", null));
        fm.addField(new TextField<String>("name", "名称"));
        fm.addField(new TextField<Integer>("sPort", "源端口"));
        fm.addField(getProtocolSelect(Protocol.TCP));
        fm.addField(getLanIpSelect("dIp", "目的IP", 10, hostId));
        fm.addField(new TextField<Integer>("dPort", "目的端口"));
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/nat/{natId}", method = RequestMethod.GET)
    @ResponseBody
    Form getNatEdit(@PathVariable long hostId, @PathVariable long natId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("firewall", "修改NAT规则[" + natId + "]", "/net/firewall/" + hostId + "/nat");
        Nat nat = hostService.getFirewallNat(natId);
        if (nat != null && nat.getHost() == hostId) {
            fm.addField(new HiddenField<>("nat", natId));
            fm.addField(new TextField<String>("name", "名称", nat.getName()));
            fm.addField(new TextField<Integer>("sPort", "源端口", nat.getsPort()));
            fm.addField(getProtocolSelect(nat.getProtocol()));
            fm.addField(getLanIpSelect("dIp", "目的IP", nat.getdIp(), hostId));
            fm.addField(new TextField<Integer>("dPort", "目的端口", nat.getdPort()));
            fm.setOk(true);
        } else {
            fm.addData("NAT规则[" + natId + "]不存在");
        }
        return fm;
    }


    @RequestMapping(value = "/nat", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postNat(@Valid NatForm form, BindingResult result, @PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            if (form.getId() == null) {
                hostService.addFirewallNat(hostId, form.getName(), form.getsPort(), form.getProtocol(), form.getdIp(), form.getdPort());
                logService.add(si.getSsAccountId(), "添加主机[" + hostId + "]NAT规则", Log.Type.INFO);
            } else {
                Nat nat = hostService.getFirewallNat(form.getId());
                if (nat != null && nat.getHost() == hostId) {
                    hostService.setFirewallNatInfo(form.getId(), form.getName());
                    hostService.setFirewallNatRule(form.getId(), form.getsPort(), form.getProtocol(), form.getdIp(), form.getdPort());
                    logService.add(si.getSsAccountId(), "修改主机[" + hostId + "]NAT规则[" + form.getId() + "]", Log.Type.INFO);
                } else {
                    ri.setOk(false);
                    ri.addData("NAT规则[" + form.getId() + "]不存在");
                }
            }

        }
        return ri;
    }

    @RequestMapping(value = "/nat/{natId}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseItem delNat(@PathVariable long hostId, @PathVariable long natId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        Nat rule = hostService.getFirewallNat(natId);
        if (rule != null && rule.getHost() == hostId) {
            hostService.delFirewallNat(natId);
            logService.add(si.getSsAccountId(), "删除主机[" + hostId + "]的NAT规则[" + natId + "]", Log.Type.INFO);
            ri.setOk(true);
        } else {
            ri.addData("NAT规则[" + natId + "]不存在");
        }
        return ri;
    }

    @RequestMapping(value = "/macOutput", method = RequestMethod.GET)
    @ResponseBody
    Form getMacOutput(@PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        List<Output> outputs = hostService.listFirewallOutputByHost(hostId);
        List<Mac> macs = new ArrayList<>();
        for (Mac m : hostService.listMacByHost(hostId)) {
            if (m.getState() == Mac.State.ENABLE) {
                macs.add(m);
            }
        }
        Host host = hostService.getHost(hostId);
        Form fm = new Form("firewall", "OUPUT例外", "/net/firewall/" + hostId + "/macOutput");
        if (outputs.size() > 0 && macs.size() > 0) {
            SelectField<Long> output = new SelectField<Long>("output", "OUPUT规则");
            for (Output out : outputs) {
                output.addOption(out.getName(), out.getId());
            }
            fm.addField(output);
            SelectField<Long> mac = new SelectField<Long>("mac", "MAC地址");
            for (Mac m : macs) {
                mac.addOption(
                        String.format("[%s.%s~%s]%s",
                                host.getLanNet(),
                                m.getIp(),
                                m.getSerial(),
                                accountService.getUser(m.getUser()).getUsername()),
                        m.getId()
                );
            }
            fm.addField(mac);

            RadioField<Boolean> enable = new RadioField<Boolean>("enable", "排除", true);
            enable.addOption("是", true);
            enable.addOption("否", false);
            fm.setOk(true);
        } else {
            fm.addData("OUTPUT规则或MAC列表信息不全");
        }
        return fm;

    }

    @RequestMapping(value = "/macOutput", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postMacOutput(@Valid MacOutputForm form, BindingResult result, @PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            Output out = hostService.getFirewallOutput(form.getOutput());
            Mac m = hostService.getMac(form.getMac());
            if (m != null && out != null && m.getHost() == hostId && out.getHost() == hostId) {

                hostService.bindMac2Output(form.getMac(), form.getOutput(), form.isEnable());
                logService.add(si.getSsAccountId(), (form.isEnable() ? "增加" : "去掉") + "OUPUT[" + form.getOutput() + "]规则与MAC[" + form.getMac() + "]关联", Log.Type.INFO);

            } else {
                ri.setOk(false);
                ri.addData("OUTPUT规则或者MAC不存在");
            }
        }
        return ri;
    }

    @RequestMapping(value = "/output", method = RequestMethod.GET)
    @ResponseBody
    Form getOutputAdd(@PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("firewall", "新增OUTPUT规则", "/net/firewall/" + hostId + "/output");
        List<DateLimit> dateLimits = hostService.listFirewallDateLimit(si.getSsCompanyId());
        if (dateLimits.size() > 0) {
            fm.addField(new HiddenField<>("id", null));
            fm.addField(new TextField<String>("name", "名称"));
            fm.addField(new TextField<String>("key", "关键字"));
            SelectField<Long> limit = new SelectField<Long>("dlLimit", "日期规则");
            for (DateLimit dl : dateLimits) {
                limit.addOption(dl.getName(), dl.getId());
            }
            fm.addField(limit);
            fm.setOk(true);
        } else {
            fm.addData("日期规则列表为空");
        }
        return fm;
    }

    @RequestMapping(value = "/output/${outId}", method = RequestMethod.GET)
    @ResponseBody
    Form getOutputEdit(@PathVariable long hostId, @PathVariable long outId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("firewall", "修改OUTPUT规则[" + outId + "]", "/net/firewall/" + hostId + "/output/" + outId);
        List<DateLimit> dateLimits = hostService.listFirewallDateLimit(si.getSsCompanyId());
        Output out = hostService.getFirewallOutput(outId);
        if (dateLimits.size() > 0 && out != null && out.getHost() == hostId) {
            fm.addField(new HiddenField<>("id", outId));
            fm.addField(new TextField<String>("name", "名称", out.getName()));
            fm.addField(new TextField<String>("key", "关键字", out.getKey()));
            SelectField<Long> limit = new SelectField<Long>("dlLimit", "日期规则", out.getDateLimit());
            for (DateLimit dl : dateLimits) {
                limit.addOption(dl.getName(), dl.getId());
            }
            fm.addField(limit);
            fm.setOk(true);
        } else {
            fm.addData("日期规则[" + outId + "]不存在");
        }
        return fm;
    }


    @RequestMapping(value = "/output", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postOutputAdd(@Valid OutputForm form, BindingResult result, @PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            if (form.getId() == null) {
                hostService.addFirewallOutput(hostId, form.getName(), form.getKey(), form.getDateLimit());
                logService.add(si.getSsAccountId(), "添加主机[" + hostId + "]OUTPUT规则", Log.Type.INFO);
            } else {
                Output out = hostService.getFirewallOutput(form.getId());
                if (out != null && out.getHost() == hostId) {
                    hostService.setFirewallOutputInfo(form.getId(), form.getName());
                    hostService.setFirewallOutputRule(form.getId(), form.getKey(), form.getDateLimit());
                    logService.add(si.getSsAccountId(), "修改主机[" + hostId + "]OUTPUT规则[" + form.getId() + "]", Log.Type.INFO);
                } else {
                    ri.setOk(false);
                    ri.addData("OUTPUT规则[" + form.getId() + "]不存在");
                }
            }
        }
        return ri;
    }


    @RequestMapping(value = "/output/{outId}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseItem delOutput(@PathVariable long hostId, @PathVariable long outId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        Output rule = hostService.getFirewallOutput(outId);
        if (rule != null && rule.getHost() == hostId) {
            hostService.delFirewallOutput(outId);
            logService.add(si.getSsAccountId(), "删除主机[" + hostId + "]的OUTPUT规则[" + outId + "]", Log.Type.INFO);
            ri.setOk(true);
        } else {
            ri.addData("OUT规则[" + outId + "]不存在");
        }
        return ri;
    }

    @RequestMapping(value = "/input", method = RequestMethod.GET)
    @ResponseBody
    Form getInputAdd(@PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("firewall", "新增INPUT规则", "/net/firewall/" + hostId + "/input");
        fm.addField(new HiddenField<>("host", hostId));
        fm.addField(new TextField<String>("name", "名称"));
        fm.addField(new TextField<String>("sIp", "源IP"));
        fm.addField(getProtocolSelect(Protocol.TCP));
        fm.addField(new TextField<Integer>("port", "目的端口"));
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/input/{inId}", method = RequestMethod.GET)
    @ResponseBody
    Form getInputAdd(@PathVariable long hostId, @PathVariable long inId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("firewall", "修改INPUT规则[" + inId + "]", "/net/firewall/" + hostId + "/input");
        Input in = hostService.getFirewallInput(inId);
        if (in != null && in.getHost() == hostId) {
            fm.addField(new HiddenField<>("id", inId));
            fm.addField(new TextField<String>("name", "名称", in.getName()));
            fm.addField(new TextField<String>("sIp", "源IP", in.getsIp()));
            fm.addField(getProtocolSelect(in.getProtocol()));
            fm.addField(new TextField<Integer>("port", "目的端口", in.getPort()));
            fm.setOk(true);
        } else {
            fm.setOk(false);
            fm.addData("INPUT规则[" + inId + "]不存在");
        }
        return fm;
    }


    @RequestMapping(value = "/input", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postInputAdd(@Valid InputAddForm form, BindingResult result, @PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            if (form.getId() == null) {
                hostService.addFirewallInput(hostId, form.getName(), form.getsIp(), form.getProtocol(), form.getPort());
                logService.add(si.getSsAccountId(), "添加主机[" + hostId + "]INPUT规则", Log.Type.INFO);
            } else {
                Input in = hostService.getFirewallInput(form.getId());
                if (in != null && in.getHost() == hostId) {
                    hostService.setFirewallInputInfo(form.getId(), form.getName());
                    hostService.setFirewallInputRule(form.getId(), form.getsIp(), form.getProtocol(), form.getPort());
                    logService.add(si.getSsAccountId(), "修改主机[" + hostId + "]上的INPUT规则[" + form.getId() + "]", Log.Type.INFO);
                } else {
                    ri.setOk(false);
                    ri.addData("INPUT规则[" + form.getId() + "]不存在");

                }
            }
        }
        return ri;
    }

    @RequestMapping(value = "/input/{inId}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseItem delInput(@PathVariable long hostId, @PathVariable long inId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        Input rule = hostService.getFirewallInput(inId);
        if (rule != null && rule.getHost() == hostId) {
            hostService.delFirewallInput(inId);
            logService.add(si.getSsAccountId(), "删除主机[" + hostId + "]的IN规则[" + inId + "]", Log.Type.INFO);
            ri.setOk(true);
        } else {
            ri.addData("IN规则[" + inId + "]不存在");
        }
        return ri;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem save(@PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        try {
            EtcFile ef = archHelper.ffProfile(hostId);
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
            rpcHelper.command(hostId, archHelper.ffClear(hostId).toArray(new String[1]));
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
            rpcHelper.command(hostId, archHelper.ffApply(hostId).toArray(new String[1]));
            ri.setOk(true);
            logService.add(si.getSsAccountId(), "应用主机[" + hostId + "]防火墙规则", Log.Type.INFO);
        } catch (Exception e) {
            ri.addData(e.getMessage());
        }
        return ri;
    }

    private SelectField<Protocol> getProtocolSelect(Protocol protocol) {
        SelectField<Protocol> sel = new SelectField<Protocol>("protocol", "协议", protocol);
        sel.addOption(Protocol.TCP.name(), Protocol.TCP);
        sel.addOption(Protocol.UDP.name(), Protocol.UDP);
        return sel;
    }

    private SelectField<Integer> getLanIpSelect(String id, String name, int ip, long hostId) {
        Host h = hostService.getHost(hostId);
        SelectField<Integer> sel = new SelectField<Integer>(id, name, ip);
        for (int i = 2; i < 255; i++) {
            sel.addOption(h.getLanNet() + "." + i, i);
        }
        return sel;
    }

    @Resource
    private HostService hostService;
    @Resource
    private LogService logService;
    @Resource
    private FormHelper formHelper;
    @Resource
    private ArchHelper archHelper;
    @Resource
    private RpcHelper rpcHelper;
    @Resource
    private AccountService accountService;
    private final static Logger logger = LoggerFactory.getLogger(FirewallController.class);

    public void setArchHelper(ArchHelper archHelper) {
        this.archHelper = archHelper;
    }

    public void setRpcHelper(RpcHelper rpcHelper) {
        this.rpcHelper = rpcHelper;
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
