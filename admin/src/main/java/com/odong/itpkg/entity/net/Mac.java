package com.odong.itpkg.entity.net;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午10:55
 */
public class Mac implements Serializable {
    private static final long serialVersionUID = -885297753742159102L;
    private Long id;
    private Long host;
    private String ip;
    private String serial;
    private String hostname;
    private Long user;
    private boolean bind;
    private Long dateLimit;
    private Long flowLimit;

    public Long getDateLimit() {
        return dateLimit;
    }

    public void setDateLimit(Long dateLimit) {
        this.dateLimit = dateLimit;
    }

    public Long getFlowLimit() {
        return flowLimit;
    }

    public void setFlowLimit(Long flowLimit) {
        this.flowLimit = flowLimit;
    }

    public Long getHost() {
        return host;
    }

    public void setHost(Long host) {
        this.host = host;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }
}
