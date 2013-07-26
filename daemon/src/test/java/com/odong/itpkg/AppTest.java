package com.odong.itpkg;

import com.odong.itpkg.util.CommandHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppTest {


    public void testServer() {
        try {
            Thread.sleep(1000 * 60 * 60 * 6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //@BeforeTest
    public void before() {
        new ClassPathXmlApplicationContext("spring/*.xml");
    }

    private void log(Object... ss) {
        for (Object s : ss) {
            System.out.println(s);
        }
    }

    private final static Logger logger = LoggerFactory.getLogger(AppTest.class);
}
