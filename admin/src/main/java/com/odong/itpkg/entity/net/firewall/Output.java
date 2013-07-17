package com.odong.itpkg.entity.net.firewall;

import com.odong.portal.entity.IdEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午11:36
 */
@Entity
@Table(name = "ffOut")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Output extends IdEntity {
    public enum Type {
        DOMAIN, KEYWORD
    }

    private static final long serialVersionUID = 8711379347940255727L;
    @Column(nullable = false, updatable = false)
    private Long host;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column(nullable = false)
    private String key;
    private Long dateLimit;
    @Column(nullable = false, updatable = false)
    private Date created;


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

    public Long getDateLimit() {
        return dateLimit;
    }

    public void setDateLimit(Long dateLimit) {
        this.dateLimit = dateLimit;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
