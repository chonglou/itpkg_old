package com.odong.itpkg.form.net;

import com.odong.itpkg.entity.net.Ip;
import com.odong.itpkg.validation.Mac;
import com.odong.itpkg.validation.Port;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-26
 * Time: 上午9:56
 */
public class HostWanForm implements Serializable {
    private static final long serialVersionUID = -4691463648032210054L;
    private int id;
    @NotNull
    @Mac
    private String wanMac;
    private String address;
    private String netmask;
    private String gateway;
    private String dns1;
    private String dns2;
    private String username;
    private String password;
    @NotNull
    private Ip.Type type;

    @Port
    private int rpcPort;

    public int getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(int rpcPort) {
        this.rpcPort = rpcPort;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWanMac() {
        return wanMac;
    }

    public void setWanMac(String wanMac) {
        this.wanMac = wanMac;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getDns1() {
        return dns1;
    }

    public void setDns1(String dns1) {
        this.dns1 = dns1;
    }

    public String getDns2() {
        return dns2;
    }

    public void setDns2(String dns2) {
        this.dns2 = dns2;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Ip.Type getType() {
        return type;
    }

    public void setType(Ip.Type type) {
        this.type = type;
    }
}
