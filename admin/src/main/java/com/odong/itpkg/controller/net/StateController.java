package com.odong.itpkg.controller.net;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.form.net.host.HostInstallForm;
import com.odong.itpkg.form.net.host.RebootForm;
import com.odong.itpkg.linux.ArchHelper;
import com.odong.itpkg.linux.EtcFile;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.rpc.Client;
import com.odong.itpkg.rpc.RpcHelper;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.service.LogService;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.Form;
import com.odong.portal.web.form.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-28
 * Time: 下午4:41
 */
@Controller("c.net.state")
@RequestMapping(value = "/net/state/{hostId}")
@SessionAttributes(SessionItem.KEY)
public class StateController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getIndex(@PathVariable long hostId, Map<String, Object> map) {
        Host host = hostService.getHost(hostId);
        map.put("wanIp", hostService.getIp(host.getWanIp()));
        map.put("defFlowLimit", hostService.getFirewallFlowLimit(host.getDefFlowLimit()));
        map.put("host", host);
        return "net/state";
    }

    @RequestMapping(value = "/reboot", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postReboot(@PathVariable long hostId) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);

        try{
            formHelper.fill(rpcHelper.command(hostId, archHelper.reboot()), ri);
            ri.setOk(true);
        }
        catch (Exception e){
            ri.addData(e.getMessage());
        }

        return ri;
    }

    @RequestMapping(value = "/install", method = RequestMethod.GET)
    @ResponseBody
    Form getInstall(@PathVariable long hostId) {
        Form fm = new Form("install", "量产系统盘", "/net/state/" + hostId + "/install");
        fm.addField(new TextField<String>("host", "主机地址"));
        fm.addField(new TextField<>("port", "RPC端口", 9999));
        fm.addField(new TextField<String>("key", "通信密钥"));
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/install", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postInstall(@PathVariable long hostId, @Valid HostInstallForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            Host host = hostService.getHost(hostId);
            String key = form.getKey();
            if("".equals(key.trim())){
                key = encryptHelper.decode(host.getSignKey());
            }
            Client client = new Client(form.getHost(), form.getPort(), key);
            try {
                //hostname配置文件
                for (EtcFile ef : archHelper.hostname(hostId)) {
                    formHelper.fill(client.send(client.file(ef.getName(), ef.getOwner(), ef.getMode(), ef.getData())), ri);
                }

                for (EtcFile ef : new EtcFile[]{
                        //itpkgd配置文件
                        archHelper.daemonProfile(hostId),
                        //防火墙规则
                        archHelper.ffProfile(hostId),
                        //限速规则
                        archHelper.tcProfile(hostId),
                        //清空规则
                        archHelper.clearProfile(hostId)
                }) {
                    formHelper.fill(client.send(client.file(ef.getName(), ef.getOwner(), ef.getMode(), ef.getData())), ri);
                }

                //网卡配置文件
                for (EtcFile ef : archHelper.networkProfile(hostId)) {
                    formHelper.fill(client.send(client.file(ef.getName(), ef.getOwner(), ef.getMode(), ef.getData())), ri);
                }
                //开机自启动服务
                formHelper.fill(client.send(client.command(archHelper.enableService(hostId).toArray(new String[1]))), ri);


                Host h = hostService.getHost(hostId);
                if (h.getState() == Host.State.SUBMIT) {
                    hostService.setHostState(hostId, Host.State.RUNNING);
                }
                logService.add(si.getSsAccountId(), "量产主机[" + hostId + "]成功", Log.Type.INFO);
            } catch (Exception e) {
                ri.setOk(false);
                ri.addData(e.getMessage());
                e.printStackTrace();
            }
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
    private EncryptHelper encryptHelper;
    @Resource
    private RpcHelper rpcHelper;
    private final static Logger logger = LoggerFactory.getLogger(StateController.class);

    public void setRpcHelper(RpcHelper rpcHelper) {
        this.rpcHelper = rpcHelper;
    }

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setArchHelper(ArchHelper archHelper) {
        this.archHelper = archHelper;
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
