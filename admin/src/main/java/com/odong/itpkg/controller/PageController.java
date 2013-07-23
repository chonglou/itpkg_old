package com.odong.itpkg.controller;

import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.LogService;
import com.odong.portal.service.SiteService;
import com.odong.portal.web.NavBar;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
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
@Controller
public class PageController {
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    String postSearch(Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        fillSiteInfo(map);
        map.put("title", "搜索结果");
        map.put("top_nav_key", "main");
        return "search";
    }

        @RequestMapping(value = "/personal/self", method = RequestMethod.GET)
    String getSelf(Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        List<NavBar> navBars = new ArrayList<>();

        NavBar nbInfo = new NavBar("个人信息");
        nbInfo.add("基本信息", "/personal/info");
        nbInfo.add("公司信息", "/company/info");
        nbInfo.add("主机列表", "/net/host");
        nbInfo.add("日志管理", "/company/log");
        nbInfo.setAjax(true);
        navBars.add(nbInfo);

        if (si.isAdmin()) {
            NavBar nbSite = new NavBar("站点管理");
            nbSite.add("公司管理", "/admin/company");
            nbSite.add("SMTP设置", "/admin/smtp");
            nbSite.add("站点信息", "/admin/info");
            nbSite.add("关于我们", "/admin/aboutMe");
            nbSite.add("注册协议", "/admin/regProtocol");
            nbSite.add("站点状态", "/admin/state");
            nbSite.add("数据压缩", "/admin/compress");
        }

        map.put("navBars", navBars);
        fillSiteInfo(map);
        map.put("title", "用户中心");
        map.put("top_nav_key", "personal/self");
        return "self";
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    String getMain(Map<String, Object> map) {
        fillSiteInfo(map);
        map.put("title", "首页");
        map.put("top_nav_key", "main");
        map.put("logs", logService.list(null));
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
        //TODO 需要Cache缓存
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
    private LogService logService;

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
}
