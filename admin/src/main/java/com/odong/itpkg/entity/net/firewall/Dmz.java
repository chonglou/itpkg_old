package com.odong.itpkg.entity.net.firewall;

import com.odong.portal.entity.IdEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午11:13
 */
@Entity
@Table(name = "ffDmz")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Dmz extends IdEntity {
    private static final long serialVersionUID = -1357313352371095441L;
    @Column(nullable = false, updatable = false)
    private Long host;
    @Column(nullable = false)
    private Long wanIp;
    @Column(nullable = false)
    private Integer lanIp;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, updatable = false)
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

    public Long getWanIp() {
        return wanIp;
    }

    public void setWanIp(Long wanIp) {
        this.wanIp = wanIp;
    }

    public Integer getLanIp() {
        return lanIp;
    }

    public void setLanIp(Integer lanIp) {
        this.lanIp = lanIp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
