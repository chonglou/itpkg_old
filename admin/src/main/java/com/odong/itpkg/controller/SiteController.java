package com.odong.itpkg.controller;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Ip;
import com.odong.itpkg.rpc.Client;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.portal.service.SiteService;
import com.odong.portal.web.ResponseItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 下午9:30
 */
@Controller
public class SiteController {
    @RequestMapping(value = "/main", method = RequestMethod.GET)
    String getMain(Map<String, Object> map) {

        return "main";
    }

    @RequestMapping(value = "/about_me", method = RequestMethod.GET)
    String getAboutMe(Map<String, Object> map) {
        return "about_me";
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    @ResponseBody
    Map<String, Object> getStatus() {
        Map<String, Object> map = new HashMap<>();
        map.put("site.startup", siteService.getObject("site.startup", Date.class));
        map.put("created", new Date());
        return map;
    }

    @RequestMapping(value = "/status/{mac}/{code}", method = RequestMethod.GET)
    @ResponseBody
    ResponseItem getHostWan(@PathVariable String mac, @PathVariable String code) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);

        try {
            Host host = hostService.getHost(mac);
            if (host == null || host.getState() == Host.State.DONE) {
                throw new IllegalArgumentException("主机不存在");
            }
            Ip wanIp = hostService.getIp(host.getWanIp());
            Client client = new Client(encryptHelper.decode(host.getSignKey()));
            String[] ip = client.decode(code).split(" ");
            if (ip.length != 5) {
                throw new IllegalArgumentException("IP地址格式不正确");
            }
            switch (wanIp.getType()) {
                case PPPOE:
                case DHCP:
                    hostService.setIpInfo(wanIp.getId(), ip[0], ip[1], ip[2], ip[3], ip[4]);
                    break;
                default:
                    throw new IllegalArgumentException("错误的IP类型[" + wanIp.getType() + "]");
            }

        } catch (Exception e) {
            ri.addData(e.getMessage());
        }
        return ri;

    }

    @RequestMapping(value = "/error/{code}", method = RequestMethod.GET)
    @ResponseBody
    Map<String, Object> getError(@PathVariable int code) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        switch (code) {
            case 404:
                map.put("message", "找不到文件");
                break;
            case 500:
                map.put("message", "服务器内部错误");
                break;
        }
        map.put("created", new Date());
        return map;
    }

    @Resource
    private SiteService siteService;
    @Resource
    private HostService hostService;
    @Resource
    private EncryptHelper encryptHelper;

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setHostService(HostService hostService) {
        this.hostService = hostService;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

}
