package com.odong.itpkg.entity.net.firewall;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午10:57
 */
public class Input implements Serializable {
    private static final long serialVersionUID = 3757284773772753042L;
    private Long id;
    private String name;
    private Long host;
    private int port;
    private Protocol protocol;
    private Long dateLimit;
    private Date created;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getDateLimit() {
        return dateLimit;
    }

    public void setDateLimit(Long dateLimit) {
        this.dateLimit = dateLimit;
    }
}
