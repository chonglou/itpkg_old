package com.odong.itpkg.job;


import com.odong.itpkg.util.CommandHelper;
import com.odong.itpkg.util.EncryptHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-20
 * Time: 下午7:24
 */
public class WanIpMonitor implements Runnable {
    public WanIpMonitor(String url, EncryptHelper encryptHelper) {
        this.url = url;
        this.encryptHelper = encryptHelper;
        client = new DefaultHttpClient();
    }

    @Override
    public void run() {
        try {
            String mac = CommandHelper.execute("ifconfig wlan | grep ether | awk -F\" \" '{print $2}'").get(0);
            String ip = CommandHelper.execute("ifconfig wlan | grep inet | awk -F\" \" '{print $2,$4,$6}'").get(0);
            List<String> dns = CommandHelper.execute("cat /etc/resolv.conf | grep nameserver | awk -F\" \" '{print $2}'");
            HttpGet get = new HttpGet(url + "/status/" + mac + "/" + encryptHelper.encode(ip + " " + dns.get(0) + " " + dns.get(1)));
            logger.debug("GET {}", get.getURI());
            HttpResponse response = client.execute(get);
            logger.debug("RESPONSE {}", response);
        }
        catch (IndexOutOfBoundsException e){
            logger.error("采集ip信息出错", e);
        }
        catch (IOException e) {
            logger.error("HTTP错误", e);
        }
    }

    private HttpClient client;
    private EncryptHelper encryptHelper;
    private String url;
    private final static Logger logger = LoggerFactory.getLogger(WanIpMonitor.class);
}
