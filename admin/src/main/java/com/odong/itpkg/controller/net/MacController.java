package com.odong.itpkg.controller.net;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.dns.Domain;
import com.odong.itpkg.entity.net.firewall.FlowLimit;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.service.LogService;
import com.odong.portal.service.SiteService;
import com.odong.portal.util.FormHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
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
    String getIndex(@PathVariable long hostId, Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Host host = hostService.getHost(hostId);
        Map<Long,FlowLimit> flowLimitMap = new HashMap<>();
        for(FlowLimit fl : hostService.listFirewallFlowLimit(host.getCompany())){
            flowLimitMap.put(fl.getId(), fl);
        }
        map.put("flowLimitMap", flowLimitMap);
        map.put("macList", hostService.listMacByHost(hostId));
        return "net/mac";
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
