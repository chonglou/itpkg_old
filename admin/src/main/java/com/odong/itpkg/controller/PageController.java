package com.odong.itpkg.controller;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.HostService;
import com.odong.portal.service.SiteService;
import com.odong.portal.web.NavBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-23
 * Time: 下午1:26
 */
@Controller("c.page")
@SessionAttributes(SessionItem.KEY)
public class PageController {
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    String postSearch(Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        fillSiteInfo(map);
        map.put("title", "搜索结果");
        map.put("top_nav_key", "main");
        return "search";
    }

    @RequestMapping(value = "/personal/self", method = RequestMethod.GET)
    String getSelf(Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        List<NavBar> navBars = new ArrayList<>();

        NavBar nbInfo = new NavBar("个人参数");
        nbInfo.add("联系信息", "/personal/info");
        nbInfo.add("修改密码", "/personal/setPwd");
        nbInfo.add("日志管理", "/personal/log");
        nbInfo.setAjax(true);
        navBars.add(nbInfo);

        NavBar nbCompany = new NavBar("公司信息");
        nbCompany.add("基本信息", "/uc/company/");
        nbCompany.add("账户管理", "/uc/account/");
        nbCompany.add("用户管理", "/uc/user/");
        nbCompany.add("主机信息", "/uc/host/");
        nbCompany.add("规则模板", "/uc/limit/");
        nbCompany.setAjax(true);
        navBars.add(nbCompany);

        NavBar nbHost = new NavBar("主机列表");
        for (Host h : hostService.listHostByCompany(si.getSsCompanyId())) {
            nbHost.add("主机-" + h.getName(), "/net/host/" + h.getId() + "/");
        }
        nbHost.setAjax(true);
        navBars.add(nbHost);

        //logger.debug("SessionItem {}", jsonHelper.object2json(si));
        if (si.isSsAdmin()) {
            NavBar nbSite = new NavBar("站点管理");
            nbSite.add("公司列表", "/admin/company/");
            nbSite.add("邮件设置", "/admin/smtp/");
            nbSite.add("站点信息", "/admin/site/info");
            nbSite.add("关于我们", "/admin/site/aboutMe");
            nbSite.add("注册协议", "/admin/site/regProtocol");
            nbSite.add("站点状态", "/admin/site/state");
            nbSite.add("验证码", "/admin/captcha/");
            nbSite.add("数据库", "/admin/database/");
            nbSite.setAjax(true);
            navBars.add(nbSite);
        }

        map.put("navBars", navBars);
        fillSiteInfo(map);
        map.put("title", "用户中心");
        map.put("top_nav_key", "personal/self");
        return "personal/self";
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    String getMain(Map<String, Object> map) {
        fillSiteInfo(map);
        map.put("title", "首页");
        map.put("top_nav_key", "main");
        List<String> logList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/Change-Logs")))) {
            String line;
            while ((line = br.readLine()) != null) {
                logList.add(line);
            }
        } catch (IOException e) {
            logger.error("加载大事记文件出错", e);
        }
        map.put("logList", logList);
        return "main";
    }

    @RequestMapping(value = "/aboutMe", method = RequestMethod.GET)
    String getAboutMe(Map<String, Object> map) {
        fillSiteInfo(map);
        map.put("title", "关于我们");
        map.put("top_nav_key", "aboutMe");
        map.put("aboutMe", siteService.getString("site.aboutMe"));
        return "aboutMe";
    }

    private void fillSiteInfo(Map<String, Object> map) {
        Map<String, Object> site = new HashMap<>();
        for (String s : new String[]{"title", "description", "copyright", "keywords", "author"}) {
            site.put(s, siteService.getString("site." + s));
        }

        Map<String, String> topNavs = new HashMap<>();
        topNavs.put("main", "站点首页");
        topNavs.put("personal/self", "用户中心");
        topNavs.put("aboutMe", "关于我们");

        site.put("topNavs", topNavs);
        map.put("gl_site", site);
    }

    @Resource
    private SiteService siteService;
    @Resource
    private HostService hostService;
    private final static Logger logger = LoggerFactory.getLogger(PageController.class);


    public void setHostService(HostService hostService) {
        this.hostService = hostService;
    }


    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
}
