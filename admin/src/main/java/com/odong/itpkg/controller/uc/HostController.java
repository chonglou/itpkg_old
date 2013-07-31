package com.odong.itpkg.controller.uc;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Ip;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.form.net.host.HostAddForm;
import com.odong.itpkg.form.net.host.HostInfoForm;
import com.odong.itpkg.form.net.host.HostLanForm;
import com.odong.itpkg.form.net.host.HostWanForm;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.service.LogService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-27
 * Time: 下午12:18
 */
@Controller("c.uc.host")
@RequestMapping(value = "/uc/host")
@SessionAttributes(SessionItem.KEY)
public class HostController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getHost(Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Map<Long, Ip> ipMap = new HashMap<>();
        List<Host> hostList = hostService.listHost(si.getSsCompanyId());
        for (Host h : hostList) {
            ipMap.put(h.getId(), hostService.getIp(h.getWanIp()));
        }
        map.put("hostList", hostList);
        map.put("ipMap", ipMap);
        return "uc/host";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @ResponseBody
    Form getHostAddForm(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("hostAdd", "添加主机", "/uc/host/add");
        fm.addField(new HiddenField<Long>("id", null));

        SelectField<Ip.Type> type = new SelectField<>("type", "类型", Ip.Type.STATIC);
        type.addOption("固定IP", Ip.Type.STATIC);
        type.addOption("动态分配", Ip.Type.DHCP);
        type.addOption("拨号上网", Ip.Type.PPPOE);
        fm.addField(type);

        String[] fields = new String[]{
                "name", "名称",
                "domain", "域",
                "wanMac", "WAN MAC",
                "address", "IP地址",
                "netmask", "子网掩码",
                "gateway", "网关",
                "dns1", "DNS1",
                "dns2", "DNS2",
                "username", "用户名",
                "password", "密码",
                "lanMac", "LAN MAC",
                "lanNet", "LAN网段"
        };
        for (int i = 0; i < fields.length; i += 2) {
            fm.addField(new TextField<String>(fields[i], fields[i + 1]));
        }

        TextField<Integer> rpcPort = new TextField<>("rpcPort", "RPC端口", 9999);
        rpcPort.setWidth(100);
        fm.addField(rpcPort);

        fm.addField(new TextAreaField("details", "详情"));
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postHostAdd(@Valid HostAddForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);

        if (form.getType() == Ip.Type.STATIC) {
            if (!formHelper.checkIp(form.getAddress())) {
                ri.setOk(false);
                ri.addData("IP地址格式不对");
            }
            if (!formHelper.checkIp(form.getNetmask())) {
                ri.setOk(false);
                ri.addData("子网掩码格式不对");
            }
            if (!formHelper.checkIp(form.getGateway())) {
                ri.setOk(false);
                ri.addData("网关地址格式不对");
            }
            if (!formHelper.checkIp(form.getDns1())) {
                ri.setOk(false);
                ri.addData("DNS1地址格式不对");
            }
            if (!formHelper.checkIp(form.getDns2())) {
                ri.setOk(false);
                ri.addData("DNS2地址格式不对");
            }
        }

        String[] ss = form.getLanNet().split("\\.");
        if (ss.length != 4 || Integer.parseInt(ss[3]) != 0) {
            ri.setOk(false);
            ri.addData("LAN网段格式不正确");
        }
        if (ri.isOk()) {
            String wanIpId = UUID.randomUUID().toString();
            switch (form.getType()) {
                case STATIC:
                    hostService.addIpStatic(wanIpId, form.getAddress(), form.getNetmask(), form.getGateway(), form.getDns1(), form.getDns2());
                    break;
                case PPPOE:
                    hostService.addIpPppoe(wanIpId, form.getUsername(), form.getPassword());
                    break;
                case DHCP:
                    hostService.addIpDhcp(wanIpId);
                    break;
            }
            hostService.addHost(si.getSsCompanyId(), form.getName(), form.getDomain(), wanIpId, form.getWanMac(), form.getRpcPort(), form.getLanNet(), form.getLanMac(), form.getDetails());
            logService.add(si.getSsAccountId(), "添加主机[" + form.getName() + "]", Log.Type.INFO);

        }
        return ri;
    }

    @RequestMapping(value = "/{host}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseItem deleteHost(@PathVariable long host, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        Host h = hostService.getHost(host);
        if (h != null && h.getCompany().equals(si.getSsCompanyId())) {
            hostService.setHostState(h.getId(), Host.State.DONE);
            logService.add(si.getSsAccountId(), "删除主机[" + host + "]", Log.Type.INFO);
            ri.setOk(true);
        }
        return ri;
    }

    @Resource
    private HostService hostService;
    @Resource
    private LogService logService;
    @Resource
    private FormHelper formHelper;

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
