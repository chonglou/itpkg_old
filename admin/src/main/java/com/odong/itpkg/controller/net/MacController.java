package com.odong.itpkg.controller.net;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Mac;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.entity.uc.User;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
        return "net/mac";
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
