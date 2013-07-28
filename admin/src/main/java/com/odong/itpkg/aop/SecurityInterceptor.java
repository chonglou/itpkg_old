package com.odong.itpkg.aop;

import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.service.RbacService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-6-13
 * Time: 下午2:56
 */
public class SecurityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String url = request.getRequestURI();
        //logger.debug("路径{}", url);
        //静态资源
        for (String s : new String[]{"bootstrap", "highcharts", "kindeditor", "style", "status", "error"}) {
            if (url.startsWith("/" + s + "/")) {
                return true;
            }
        }
        for (String s : new String[]{"/", "/main", "/aboutMe", "/status", "/captcha"}) {
            if (url.equals(s)) {
                return true;
            }
        }
        SessionItem si = (SessionItem) request.getSession().getAttribute(SessionItem.KEY);
        //personal
        if (url.startsWith("/personal")) {
            boolean notNeedLogin = false;
            String[] ss = url.split("/");
            for (String s : new String[]{"login", "register", "resetPwd", "active", "valid"}) {
                if (s.equals(ss[2])) {
                    notNeedLogin = true;
                    break;
                }
            }

            if (si == null && !notNeedLogin) {
                login(response);
                return false;
            }
            if (si != null && notNeedLogin) {
                notFound(response);
                return false;
            }
            return true;
        }
        //js
        if (url.startsWith("/js/")) {
            if (si == null) {
                for (String s : new String[]{"personal", "admin"}) {
                    if (url.equals("/js/" + s + ".js")) {
                        notFound(response);
                        return false;
                    }
                }
            } else {
                if (
                        url.equals("/js/non-login.js") ||
                                (url.equals("/js/admin.js") && !si.isSsAdmin())
                        ) {
                    notFound(response);
                    return false;
                }
            }
            return true;
        }
        //uc
        if (url.startsWith("/company/")) {
            if (si == null) {
                login(response);
                return false;
            }
            if (url.startsWith("/company/manage/") && !si.isSsCompanyManager()) {
                notFound(response);
                return false;
            }
            return true;
        }

        //admin
        if (url.startsWith("/admin/")) {
            if (si == null) {
                login(response);
                return false;

            }
            if (si.isSsAdmin()) {
                return true;
            }
            notFound(response);
            return false;
        }
        //net
        if (url.startsWith("/net/")) {
            if (si == null) {
                login(response);
                return false;
            }
            String[] ss = url.split("/");
            for (String s : new String[]{"host", "limit"}) {
                if (ss[2].equals(s)) {
                    return true;
                }
            }

            for (String s : new String[]{"bind9", "dhcp4", "firewall", "mac"}) {
                if (ss[2].equals(s)) {
                    Long hostId = Long.parseLong(ss[3]);
                    if (hostService.getHost(hostId).getCompany().equals(si.getSsCompanyId())) {
                        return true;
                    } else {
                        notFound(response);
                        return false;
                    }
                }
            }
            return true;
        }
        //search
        if (url.equals("/search")) {
            if (si == null) {
                login(response);
                return false;
            }
            return true;
        }

        notFound(response);
        return false;  //
    }

    private void login(HttpServletResponse response) throws IOException {
        response.sendRedirect("/");
    }

    private void notFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {
        //
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) throws Exception {
        //
    }

    private final static Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);

    private RbacService rbacService;
    private HostService hostService;

    public void setRbacService(RbacService rbacService) {
        this.rbacService = rbacService;
    }

    public void setHostService(HostService hostService) {
        this.hostService = hostService;
    }
}
