package com.odong.itpkg.controller.net;

import com.odong.itpkg.entity.net.dns.Domain;
import com.odong.itpkg.entity.net.dns.Zone;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.form.net.DomainForm;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.service.LogService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.Form;
import com.odong.portal.web.form.HiddenField;
import com.odong.portal.web.form.TextAreaField;
import com.odong.portal.web.form.TextField;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
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
        map.put("zoneList", hostService.listDnsZone(hostId));
        map.put("domainMap", domainMap);
        return "net/bind9";
    }

    @RequestMapping(value = "/zone", method = RequestMethod.GET)
    @ResponseBody
    Form getAddZoneAddFrom(@PathVariable long hostId) {
        Form fm = new Form("addZone", "添加主域名", "/net/bind9/" + hostId + "/zone");
        fm.addField(new HiddenField<Long>("zone", null));
        fm.addField(new TextField<>("name", "域名"));
        fm.addField(new TextAreaField("details", "详情"));
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/{hostId}/zone/{zoneId}", method = RequestMethod.GET)
    @ResponseBody
    Form getAddZoneEditForm(@PathVariable long hostId, @PathVariable long zoneId) {
        Zone z = hostService.getDnsZone(zoneId);
        Form fm = new Form("setZone", "修改域名[" + z.getName() + "]", "/" + hostId + "/zone");
        fm.addField(new HiddenField<>("zone", zoneId));
        TextField<String> name = new TextField<>("name", "域名");
        name.setReadonly(true);
        fm.addField(name);
        fm.addField(new TextAreaField("details", "详情"));
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/{hostId}/zone", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postZone(@PathVariable long hostId, @Valid DomainForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            if (form.getZone() == null) {
                if (hostService.getDnsZone(form.getName(), hostId) == null) {
                    hostService.addDnsZone(hostId, form.getName(), form.getDetails());
                    logService.add(si.getSsAccountId(), "添加主域名[" + form.getName() + "]", Log.Type.INFO);
                } else {
                    ri.setOk(false);
                    ri.addData("主域名[" + form.getName() + "]已存在");
                }
            } else {
                hostService.setDnsZone(form.getZone(), form.getDetails());
                logService.add(si.getSsAccountId(), "更新主域名[" + form.getName() + "]信息", Log.Type.INFO);
            }
        }
        return ri;
    }

    @RequestMapping(value = "/{hostId}/zone/{zoneId}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseItem delZone(@PathVariable long hostId, @PathVariable long zoneId) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        Zone z = hostService.getDnsZone(zoneId);
        if (z != null && z.getHost() == hostId) {
            hostService.delDnsZone(zoneId);
            ri.setOk(true);
        }
        ri.addData("域名[" + zoneId + "]不存在");
        return ri;
    }

    @Resource
    private FormHelper formHelper;
    @Resource
    private HostService hostService;
    @Resource
    private LogService logService;

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
