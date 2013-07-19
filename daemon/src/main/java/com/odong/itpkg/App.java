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

        if (!"Oracle Corporation".equals(System.getProperty("java.vm.vendor")) || !"Linux".equals(System.getProperty("os.name")) || !"1.7".equals(System.getProperty("java.vm.specification.version"))) {
            logger.error("当前只支持Oracle JDK v1.7 For  Linux x64\n下载地址在：http://www.oracle.com/technetwork/java/javase/downloads/index.html");
            System.exit(-1);
        }

    }

    @Override
    public void start() throws Exception {
        logger.info("正在启动...");
        ctx = new ClassPathXmlApplicationContext("spring/*.xml");


    }

    @Override
    public void stop() throws Exception {
        logger.info("正在停止...");
        ((ClassPathXmlApplicationContext) ctx).stop();
    }

    @Override
    public void destroy() {
        logger.debug("正在清理...");
    }


    private ApplicationContext ctx;
    private final static Logger logger = LoggerFactory.getLogger(App.class);
}
