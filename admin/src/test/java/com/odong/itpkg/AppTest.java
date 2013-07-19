package com.odong.itpkg;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.odong.itpkg.model.Rpc;
import com.odong.itpkg.rpc.Callback;
import com.odong.itpkg.rpc.Client;
import com.odong.itpkg.util.JsonHelper;
import com.odong.itpkg.util.SSHHelper;
import com.odong.itpkg.util.StringHelper;
import com.odong.itpkg.util.impl.JsonHelperImpl;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AppTest {
    @Test
    public void testClient() {

        String key = "Qui9eeghen5AN5quave4elix7ahc";
        int len = 12;
        String host = "localhost";
        int port = 9999;
        Client client = new Client(host, port, key, len, new Callback() {
            @Override
            public void execute(Rpc.Response response) {
                System.out.println(response);
            }
        });
        /*
        for (int i = 0; i < 10; i++) {
            log("#################### " + i + " #########################");
            client.send(client.heart());
        }
        */
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            lines.add("echo " + i);
        }
        client.send(client.command(lines));
        client.send(client.file("/tmp/aaa/bbb/itpkg.conf", "rw-r--r--", lines));

        client.send(client.bye());
        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {

        }
    }

    private void log(Object... objects) {
        for (Object o : objects) {
            System.out.println(jsonHelper.object2json(o));
        }
    }

    @BeforeTest
    void before() {
        jsonHelper = new JsonHelperImpl();
        ((JsonHelperImpl) jsonHelper).init();
        stringHelper = new StringHelper();
        stringHelper.init();
    }

    private JsonHelper jsonHelper;
    private StringHelper stringHelper;
}
