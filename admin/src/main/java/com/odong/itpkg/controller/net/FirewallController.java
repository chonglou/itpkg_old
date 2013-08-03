package com.odong.itpkg.controller.net;

import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.linux.ArchHelper;
import com.odong.itpkg.linux.EtcFile;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.rpc.RpcHelper;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.service.LogService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
        map.put("outList", hostService.listFirewallOutputByHost(hostId));
        map.put("inList", hostService.listFirewallInput(hostId));
        map.put("natList", hostService.listFirewallNat(hostId));
        return "net/firewall";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem save(@PathVariable long hostId, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        try {
            EtcFile ef = archHelper.ffProfile(hostId);
            rpcHelper.file(hostId, ef.getName(),ef.getOwner(), ef.getMode(), ef.getData());
            ri.setOk(true);
            logService.add(si.getSsAccountId(), "保存主机["+hostId+"]防火墙规则", Log.Type.INFO);
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
            logService.add(si.getSsAccountId(), "清空主机["+hostId+"]防火墙规则", Log.Type.INFO);
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
            logService.add(si.getSsAccountId(), "应用主机["+hostId+"]防火墙规则", Log.Type.INFO);
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
    private FormHelper formHelper;  @Resource
    private ArchHelper archHelper;
    @Resource
    private RpcHelper rpcHelper;
    private final static Logger logger = LoggerFactory.getLogger(FirewallController.class);

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
