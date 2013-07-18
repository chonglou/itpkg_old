package com.odong.itpkg;

import com.odong.itpkg.util.CommandHelper;
import com.odong.itpkg.util.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

public class AppTest {

    @Test
    public void testNet() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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
