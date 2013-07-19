package com.odong.itpkg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;

public class AppTest {


    public void testServer() {
        try {
            Thread.sleep(1000 * 60 * 60 * 6);
        } catch (InterruptedException e) {
        }

    }

    @BeforeTest
    public void before() {
        new ClassPathXmlApplicationContext("spring/*.xml");
    }

    private void log(String... ss) {
        for (String s : ss) {
            System.out.println(s);
        }
    }

    private final static Logger logger = LoggerFactory.getLogger(AppTest.class);
}
