package com.odong.itpkg;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App implements Daemon {
    @Override
    public void init(DaemonContext daemonContext) throws DaemonInitException, Exception {
        logger.info("正在初始化...");
    }

    @Override
    public void start() throws Exception {
        logger.info("正在启动...");
        ctx = new ClassPathXmlApplicationContext("spring/*.xml");


    }

    @Override
    public void stop() throws Exception {
        logger.info("正在停止...");
        ((ClassPathXmlApplicationContext)ctx).stop();
    }

    @Override
    public void destroy() {
        logger.debug("正在清理...");
    }

    private ApplicationContext ctx;
    private final static Logger logger = LoggerFactory.getLogger(App.class);
}
