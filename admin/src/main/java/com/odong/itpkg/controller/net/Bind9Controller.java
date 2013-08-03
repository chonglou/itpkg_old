package com.odong.itpkg.controller.net;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.dns.Domain;
import com.odong.itpkg.entity.net.dns.Zone;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.form.net.dns.DomainForm;
import com.odong.itpkg.form.net.dns.ZoneForm;
import com.odong.itpkg.linux.ArchHelper;
import com.odong.itpkg.linux.EtcFile;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.rpc.RpcHelper;
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
@Controller("c.net.bind9")
@RequestMapping(value = "/net/bind9/{hostId}")
@SessionAttributes(SessionItem.KEY)
public class Bind9Controller {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getIndex(@PathVariable long hostId, Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {

        Map<Long, Object> domainMap = new HashMap<>();
        for (Domain d : hostService.listDnsDomainByHost(hostId)) {
            domainMap.put(d.getId(), d);
        }
        List<String> logs = new ArrayList<>();
        try {
            logs.addAll(rpcHelper.command(hostId, archHelper.statusDncp4()).getLinesList());
        } catch (Exception e) {
            logs.add(e.getMessage());
        }
        map.put("logs", logs);
        map.put("zoneList", hostService.listDnsZone(hostId));
        map.put("domainMap", domainMap);
        map.put("host", hostService.getHost(hostId));
        return "net/bind9";
    }

    @RequestMapping(value = "/zone/add", method = RequestMethod.GET)
    @ResponseBody
    Form getZoneAddFrom(@PathVariable long hostId) {
        Form fm = new Form("named", "添加主域名", "/net/bind9/" + hostId + "/zone");
        fm.addField(new HiddenField<Long>("id", null));
        fm.addField(new TextField<>("name", "域名"));
        fm.addField(new TextAreaField("details", "详情"));
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/zone/{zoneId}", method = RequestMethod.GET)
    @ResponseBody
    Form getZoneEditForm(@PathVariable long hostId, @PathVariable long zoneId) {
        Zone z = hostService.getDnsZone(zoneId);
        Form fm = new Form("named", "修改域名[" + zoneId + "]", "/net/bind9/" + hostId + "/zone");
        if (z != null && z.getHost() == hostId) {

            fm.addField(new HiddenField<>("id", zoneId));
            TextField<String> name = new TextField<>("name", "域名", z.getName());
            name.setReadonly(true);
            fm.addField(name);
            fm.addField(new TextAreaField("details", "详情", z.getDetails()));
            fm.setOk(true);
        } else {
            fm.addData("主域名[" + zoneId + "]不存在");
        }
        return fm;
    }

    @RequestMapping(value = "/zone", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postZone(@PathVariable long hostId, @Valid ZoneForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            if (form.getId() == null) {
                if (hostService.getDnsZone(form.getName(), hostId) == null) {
                    hostService.addDnsZone(hostId, form.getName(), form.getDetails());
                    logService.add(si.getSsAccountId(), "添加主域名[" + form.getName() + "]", Log.Type.INFO);
                } else {
                    ri.setOk(false);
                    ri.addData("主域名[" + form.getName() + "]已存在");
                }
            } else {
                Zone z = hostService.getDnsZone(form.getId());
                if (z != null && z.getHost() == hostId) {
                    hostService.setDnsZone(form.getId(), form.getDetails());
                    logService.add(si.getSsAccountId(), "更新主域名[" + form.getName() + "]信息", Log.Type.INFO);
                } else {
                    ri.setOk(false);
                    ri.addData("主域名[" + form.getId() + "]不存在");
                }
            }
        }
        return ri;
    }

    @RequestMapping(value = "/zone/{zoneId}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseItem delZone(@PathVariable long hostId, @PathVariable long zoneId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        Zone z = hostService.getDnsZone(zoneId);
        if (z != null && z.getHost() == hostId) {
            hostService.delDnsZone(zoneId);
            logService.add(si.getSsAccountId(), "删除主域名["+z.getName()+"]", Log.Type.INFO);
            ri.setOk(true);
        } else {
            ri.addData("主域名[" + zoneId + "]不存在");
        }
        return ri;
    }

    @RequestMapping(value = "/domain/add", method = RequestMethod.GET)
    @ResponseBody
    Form getDomainAddFrom(@PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("named", "添加次域名", "/net/bind9/" + hostId + "/domain");
        List<Zone> zoneList = hostService.listDnsZone(hostId);
        if (zoneList.size() > 0) {
            SelectField<Long> zone = new SelectField<Long>("zone", "主域名");
            for (Zone z :zoneList) {
                zone.addOption(z.getName(), z.getId());
            }
            fm.addField(zone);

            fm.addField(new TextField<>("name", "名称"));

            SelectField<Domain.Type> type = new SelectField<Domain.Type>("type", "类型", Domain.Type.A);
            type.addOption("A记录", Domain.Type.A);
            type.addOption("MX记录", Domain.Type.MX);
            type.addOption("NS记录", Domain.Type.NS);
            fm.addField(type);

            SelectField<Integer> priority = new SelectField<>("priority", "优先级", 3);
            for(int i=1; i<11;i++){
                priority.addOption(""+i,i);
            }
            fm.addField(priority);

            RadioField<Boolean> local = new RadioField<>("local", "内网", true);
            local.addOption("是", true);
            local.addOption("否", false);
            fm.addField(local);

            Host host = hostService.getHost(hostId);
            SelectField<Integer> lanIp = new SelectField<>("lanIp", "IP", 10);
            for(int i=2; i<255; i++){
                lanIp.addOption(host.getLanNet()+"."+i,i);
            }
            fm.addField(lanIp);

            fm.addField(new TextField<String>("wanIp", "IP"));

            fm.setOk(true);
        } else {
            fm.addData("主域名列表为空");
        }
        return fm;
    }


    @RequestMapping(value = "/domain", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postDomain(@PathVariable long hostId, @Valid DomainForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        Zone z = hostService.getDnsZone(form.getZone());

        if(z!=null && z.getHost()==hostId){
            Domain d = hostService.getDnsDomain(form.getName(), form.getZone());
            if(d != null){
                ri.setOk(false);
                ri.addData("域名"+d.getName()+"."+z.getName()+"已存在");
            }
        }
        else {
            ri.setOk(false);
            ri.addData("主域名["+form.getZone()+"]不存在");
        }
        if(!form.isLocal()&&form.getWanIp().trim().equals("")){
            ri.setOk(false);
            ri.addData("IP地址不能为空");
        }

        if (ri.isOk()) {
            switch (form.getType()){
                case MX:
                    if(form.isLocal()){
                        hostService.addDnsDomainMX(form.getZone(), form.getName(), form.getLanIp(), form.getPriority());
                    }
                        else
                    {
                        hostService.addDnsDomainMX(form.getZone(), form.getName(), form.getWanIp(), form.getPriority());
                    }
                    break;
                case A:
                    if(form.isLocal()){
                        hostService.addDnsDomainA(form.getZone(), form.getName(), form.getLanIp());
                    }
                    else {
                        hostService.addDnsDomainA(form.getZone(), form.getName(), form.getWanIp());
                    }
                    break;
                case NS:
                    if(form.isLocal()){
                        hostService.addDnsDomainNS(form.getZone(), form.getName(), form.getLanIp());
                    }
                    else {
                        hostService.addDnsDomainNS(form.getZone(), form.getName(), form.getWanIp());
                    }
                    break;
                default:
                    ri.setOk(false);
                    ri.addData("未知的域名类型");
                    break;

            }


        }
        if(ri.isOk()){
            logService.add(si.getSsAccountId(), "添加次域名解析"+form.getName()+"@"+form.getZone(), Log.Type.INFO);
        }
        return ri;
    }

    @RequestMapping(value = "/domain/{domainId}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseItem delDomain(@PathVariable long hostId, @PathVariable long domainId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        Domain d = hostService.getDnsDomain(domainId);
        if (d != null) {
            Zone z = hostService.getDnsZone(d.getZone());
            if (z != null && z.getHost() == hostId) {
                hostService.delDnsDomain(domainId);
                logService.add(si.getSsAccountId(), "删除次级域名"+d.getName()+"."+z.getName(), Log.Type.INFO);
                ri.setOk(true);
            }
        }
        if (!ri.isOk()) {
            ri.addData("次域[" + domainId + "]不存在");
        }
        return ri;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem saveBind9(@PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        try {
            for (EtcFile ef : archHelper.bind9Profile(hostId)) {
                rpcHelper.file(hostId, ef.getName(), ef.getOwner(), ef.getMode(), ef.getData());
            }
            ri.setOk(true);
            logService.add(si.getSsAccountId(), "保存DNS配置", Log.Type.INFO);
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
            rpcHelper.command(hostId, archHelper.startBind9());
            ri.setOk(true);
            logService.add(si.getSsAccountId(), "启动DNS服务", Log.Type.INFO);
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
            rpcHelper.command(hostId, archHelper.stopBind9());
            ri.setOk(true);
            logService.add(si.getSsAccountId(), "停止DNS服务", Log.Type.INFO);
        } catch (Exception e) {
            ri.addData(e.getMessage());
        }
        return ri;
    }

    @Resource
    private FormHelper formHelper;
    @Resource
    private HostService hostService;
    @Resource
    private LogService logService;
    @Resource
    private ArchHelper archHelper;
    @Resource
    private RpcHelper rpcHelper;
    private final static Logger logger = LoggerFactory.getLogger(Bind9Controller.class);

    public void setArchHelper(ArchHelper archHelper) {
        this.archHelper = archHelper;
    }

    public void setRpcHelper(RpcHelper rpcHelper) {
        this.rpcHelper = rpcHelper;
    }

    public void setFormHelper(FormHelper formHelper) {
        this.formHelper = formHelper;
    }

    public void setHostService(HostService hostService) {
        this.hostService = hostService;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }
}
