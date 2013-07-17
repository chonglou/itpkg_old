package com.odong.itpkg.entity.net;

import com.odong.portal.entity.IdEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午11:09
 */
@Entity
@Table(name = "netIp")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Ip extends IdEntity {
    public enum Type {
        PPPOE, DHCP, STATIC
    }

    private static final long serialVersionUID = 4176678672721576398L;
    @Column(nullable = false, updatable = false)
    private Long host;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;
    private String username;
    private String password;
    private String address;
    private String netmask;
    private String gateway;
    private String dns1;
    private String dns2;
    @Version
    private int version;


    public Long getHost() {
        return host;
    }

    public void setHost(Long host) {
        this.host = host;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
}
