package com.odong.itpkg.controller.net;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Mac;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.form.net.Dhcp4Form;
import com.odong.itpkg.linux.ArchHelper;
import com.odong.itpkg.linux.EtcFile;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.rpc.RpcHelper;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.service.LogService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.Form;
import com.odong.portal.web.form.RadioField;
import com.odong.portal.web.form.SelectField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午11:27
 */

@Controller("c.net.dhcp4")
@RequestMapping(value = "/net/dhcp4/{hostId}")
@SessionAttributes(SessionItem.KEY)
public class Dhcp4Controller {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getIndex(@PathVariable long hostId, Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        List<Mac> macList = new ArrayList<>();
        for (Mac m : hostService.listMacByHost(hostId)) {
            if (m.getState() == Mac.State.ENABLE) {
                macList.add(m);
            }
        }
        List<String> logs = new ArrayList<>();
        try {
            logs.addAll(rpcHelper.command(hostId, archHelper.statusDncp4()).getLinesList());
        } catch (Exception e) {
            logs.add(e.getMessage());
        }
        map.put("logs", logs);
        map.put("macList", macList);
        map.put("host", hostService.getHost(hostId));
        return "net/dhcp4";
    }

    @RequestMapping(value = "/{macId}", method = RequestMethod.GET)
    @ResponseBody
    Form getDhcp4(@PathVariable long hostId, @PathVariable long macId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("dhcp4", "修改MAC[" + macId + "]", "/net/dhcp4/" + hostId + "/" + macId);
        Mac mac = hostService.getMac(macId);
        Host h = hostService.getHost(hostId);
        if (mac != null && mac.getHost() == hostId) {
            SelectField<Integer> ip = new SelectField<Integer>("ip", "IP", mac.getIp());
            for (int i = 2; i < 255; i++) {
                ip.addOption(h.getLanNet() + "." + i, i);
            }
            fm.addField(ip);
            RadioField<Boolean> bind = new RadioField<Boolean>("bind", "类型", mac.isBind());
            bind.addOption("固定", true);
            bind.addOption("动态", false);
            fm.addField(bind);
            fm.setOk(true);
        } else {
            fm.addData("MAC[" + macId + "]不存在");
        }
        return fm;
    }

    @RequestMapping(value = "/{macId}", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postDhcp4(@Valid Dhcp4Form form, BindingResult result, @PathVariable long hostId, @PathVariable long macId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            Mac mac = hostService.getMac(macId);
            Host host = hostService.getHost(hostId);
            if (mac != null && mac.getHost() == hostId) {
                for (Mac m : hostService.listMacByHost(hostId)) {
                    if (m.isBind() && m.getIp() == form.getIp()) {
                        ri.setOk(false);
                        ri.addData("IP " + host.getLanNet() + "." + form.getIp() + "已占用");
                        break;
                    }
                }
            } else {
                ri.setOk(false);
                ri.addData("MAC[" + macId + "]不存在");
            }

        }
        if (ri.isOk()) {
            hostService.bindIp2Mac(macId, form.getIp(), form.isBind());
            logService.add(si.getSsAccountId(), (form.isBind() ? "绑定" : "解邦") + macId, Log.Type.INFO);
        }
        return ri;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem saveDhcp4(@PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        try {
            EtcFile ef = archHelper.dhcpdProfile(hostId);
            rpcHelper.file(hostId, ef.getName(), ef.getOwner(), ef.getMode(), ef.getData());
            ri.setOk(true);
            logService.add(si.getSsAccountId(), "保存DHCP配置", Log.Type.INFO);
        } catch (Exception e) {
            ri.addData(e.getMessage());
        }
        return ri;
    }

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem startDhcp4(@PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        try {
            rpcHelper.command(hostId, archHelper.startDncp4());
            ri.setOk(true);
            logService.add(si.getSsAccountId(), "启动主机[" + hostId + "]DHCP服务", Log.Type.INFO);
        } catch (Exception e) {
            ri.addData(e.getMessage());
        }
        return ri;
    }

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem stopDhcp4(@PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        try {
            rpcHelper.command(hostId, archHelper.stopDncp4());
            ri.setOk(true);
            logService.add(si.getSsAccountId(), "停止DHCP服务", Log.Type.INFO);
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
    private FormHelper formHelper;
    @Resource
    private ArchHelper archHelper;
    @Resource
    private RpcHelper rpcHelper;
    private final static Logger logger = LoggerFactory.getLogger(Dhcp4Controller.class);

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
