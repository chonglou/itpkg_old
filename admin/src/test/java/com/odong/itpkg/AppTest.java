package com.odong.itpkg;


import com.odong.itpkg.model.Rpc;
import com.odong.itpkg.rpc.Callback;
import com.odong.itpkg.rpc.Client;
import com.odong.itpkg.util.JsonHelper;
import com.odong.itpkg.util.StringHelper;
import com.odong.itpkg.util.impl.JsonHelperImpl;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class AppTest {
    @Test
    public void testClient() {

        String key = "Qui9eeghen5AN5quave4elix7ahc";
        int len = 12;
        String host = "localhost";
        int port = 9999;

        for (int i = 0; i < 10; i++) {
            log("#################### " + i + " #########################");
            Client client = new Client(host, port, key, len, new Callback() {
                @Override
                public void execute(Rpc.Response response) {
                    System.out.println(response);
                }
            });
            client.send(client.heart());
            client.bye();
        }
        log("fuck");
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
