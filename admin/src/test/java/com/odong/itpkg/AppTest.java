package com.odong.itpkg;


import com.odong.itpkg.util.JsonHelper;
import com.odong.itpkg.util.StringHelper;
import com.odong.itpkg.util.impl.JsonHelperImpl;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class AppTest {

    @Test
    public void setName(){

    }

    public void testClient() {


        String key = "Qui9eeghen5AN5quave4elix7ahc";
        String host = "192.168.1.102";
        int port = 9999;


        /*
        Client client = new Client(key);
        final List<String> list = new ArrayList<>();
        client.open(host, port, new Callback() {
            @Override
            public void execute(Rpc.Response response) {
                System.out.println("收到");
                for(String s : response.getLinesList()){
                    list.add(s);
                }
            }
        });


        for (int i = 0; i < 10; i++) {
            log("#################### " + i + " #########################");
            client.send(client.heart());
            while (list.size() == 0){
                try{
                Thread.sleep(1000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            list.clear();
        }

        */
        /*
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
        */

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
