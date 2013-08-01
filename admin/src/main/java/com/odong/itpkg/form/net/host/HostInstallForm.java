package com.odong.itpkg.form.net.host;

import com.odong.itpkg.validation.IpV4;
import com.odong.itpkg.validation.Mac;
import com.odong.itpkg.validation.Port;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-1
 * Time: 上午10:11
 */
public class HostInstallForm implements Serializable {
    private static final long serialVersionUID = 6221543332115847729L;
    @NotNull
    @IpV4
    private String host;
    @NotNull
    @Port
    private int port;
    @NotNull(message = "{valid.notnull}")
    private String key;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
