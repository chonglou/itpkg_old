package com.odong.itpkg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class AppTest {

    @Test
    public void testServer() {
        try {

            Thread.sleep(1000 * 60 * 60 * 6);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @BeforeTest
    public void before() {
        try {
            new ClassPathXmlApplicationContext("spring/*.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void log(Object... ss) {
        for (Object s : ss) {
            System.out.println(s);
        }
    }

    private final static Logger logger = LoggerFactory.getLogger(AppTest.class);
}
