package com.odong.itpkg.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-6-10
 * Time: 下午1:33
 */
public class ExceptionHandler implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception ex) {
        logger.error("servlet异常", ex);
        Map<String, Object> map = new HashMap<>();
        map.put("message", ex.getMessage());
        map.put("created", new Date());
        return new ModelAndView("jsonView", map);
    }

    private final static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);
}
