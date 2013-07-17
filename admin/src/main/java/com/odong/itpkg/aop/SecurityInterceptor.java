package com.odong.itpkg.aop;

import com.odong.itpkg.model.SessionItem;
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
        SessionItem si = (SessionItem) request.getSession().getAttribute(SessionItem.KEY);
        if (url.startsWith("/admin/") || url.equals("/js/admin.js")) {
            if (si == null) {
                login(response);
                return false;

            } else if (si.isAdmin()) {
                return true;
            }
            notFound(response);
            return false;
        }


        if (url.startsWith("/personal/")) {
            boolean notNeedLogin = false;
            for (String s : new String[]{"login", "register", "reset_pwd"}) {
                if (url.startsWith("/personal/" + s)) {
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
        if (si == null && url.equals("/js/personal.js")) {
            notFound(response);
            return false;
        }
        if (si != null && url.equals("/js/non-login.js")) {
            notFound(response);
            return false;
        }

        return true;  //
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


}
