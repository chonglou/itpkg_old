package com.odong.itpkg;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class AppTest {

    @Test
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
}
