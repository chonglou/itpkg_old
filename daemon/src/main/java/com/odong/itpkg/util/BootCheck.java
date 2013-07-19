package com.odong.itpkg.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午2:44
 */
@Component("bootCheck")
public class BootCheck {

    @PostConstruct
    void init() {
        if (!"root".equals(System.getProperty("user.name"))) {
            logger.warn("调试启动");
        }
    }

    @Value("${server.wan}")
    private String wan;
    private final static Logger logger = LoggerFactory.getLogger(BootCheck.class);

    public void setWan(String wan) {
        this.wan = wan;
    }
}
