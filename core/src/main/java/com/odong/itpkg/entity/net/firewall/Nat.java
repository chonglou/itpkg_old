package com.odong.itpkg.entity.net.firewall;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午11:33
 */
public class Nat implements Serializable {
    private static final long serialVersionUID = 3006413465282267942L;
    private Long host;
    private int port;
    private Protocol protocol;
    private Long mac;
    private Long dateLimit;
    private Date created;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getHost() {
        return host;
    }

    public void setHost(Long host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public Long getMac() {
        return mac;
    }

    public void setMac(Long mac) {
        this.mac = mac;
    }

    public Long getDateLimit() {
        return dateLimit;
    }

    public void setDateLimit(Long dateLimit) {
        this.dateLimit = dateLimit;
    }
}
