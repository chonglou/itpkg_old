package com.odong.itpkg;


import com.odong.itpkg.model.Rpc;
import com.odong.itpkg.rpc.Callback;
import com.odong.itpkg.rpc.Client;
import com.odong.itpkg.util.JsonHelper;
import com.odong.itpkg.util.StringHelper;
import com.odong.itpkg.util.impl.JsonHelperImpl;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class AppTest {
    @Test
    public void testFormat(){
        log(String.format("%02d:%02d", 1, 1));
    }
    //@Test
    public void testClient() {

        String key = "Qui9eeghen5AN5quave4elix7ahc";
        String host = "192.168.1.102";
        int port = 9999;
        Client client = new Client(key);
        client.open(host, port, new Callback() {
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
        client.send(client.file("/etc/itpkg.conf", "flamen:users", "444", lines));

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
