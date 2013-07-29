package com.odong.itpkg.controller.net;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.firewall.FlowLimit;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.service.LogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-28
 * Time: 下午4:41
 */
@Controller
@RequestMapping(value = "/net/state/{hostId}")
@SessionAttributes(SessionItem.KEY)
public class StateController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getIndex(@PathVariable long hostId, Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Host host = hostService.getHost(hostId);
        return "net/state";
    }

    @Resource
    private LogService logService;
    @Resource
    private HostService hostService;

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setHostService(HostService hostService) {
        this.hostService = hostService;
    }
}
