package com.odong.itpkg.form.net;

import com.odong.itpkg.entity.net.Ip;
import com.odong.itpkg.validation.IpV4;
import com.odong.itpkg.validation.Mac;
import com.odong.itpkg.validation.Port;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-24
 * Time: 下午4:55
 */
public class HostAddForm implements Serializable {
    private static final long serialVersionUID = 1955486138712687874L;
    @NotNull
    @Size(min = 2, max = 20)
    private String name;
    @NotNull
    @Size(min = 2, max = 20)
    private String domain;
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
    @NotNull
    @Mac
    private String lanMac;
    @NotNull
    @IpV4
    private String lanNet;
    @Port
    private int rpcPort;
    private String details;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

    public String getLanMac() {
        return lanMac;
    }

    public void setLanMac(String lanMac) {
        this.lanMac = lanMac;
    }

    public String getLanNet() {
        return lanNet;
    }

    public void setLanNet(String lanNet) {
        this.lanNet = lanNet;
    }

    public int getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(int rpcPort) {
        this.rpcPort = rpcPort;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
