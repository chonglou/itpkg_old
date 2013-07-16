package com.odong.itpkg.entity.net.firewall;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午11:36
 */
public class Output implements Serializable {
    public enum Type {
        DOMAIN
    }

    private static final long serialVersionUID = 8711379347940255727L;
    private Long id;
    private Long host;
    private Type type;
    private String key;
    private DateLimit dateLimit;
    private Date created;

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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DateLimit getDateLimit() {
        return dateLimit;
    }

    public void setDateLimit(DateLimit dateLimit) {
        this.dateLimit = dateLimit;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
