package com.odong.itpkg.entity.net.firewall;

import com.odong.portal.entity.IdEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午11:33
 */
@Entity
@Table(name = "ffNat")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Nat extends IdEntity {
    private static final long serialVersionUID = 3006413465282267942L;
    @Column(nullable = false, updatable = false)
    private Long host;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private int sPort;
    @Column(nullable = false)
    private int dPort;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Protocol protocol;
    @Column(nullable = false)
    private int dIp;
    private Long dateLimit;
    @Column(nullable = false, updatable = false)
    private Date created;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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


    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public int getsPort() {
        return sPort;
    }

    public void setsPort(int sPort) {
        this.sPort = sPort;
    }

    public int getdPort() {
        return dPort;
    }

    public void setdPort(int dPort) {
        this.dPort = dPort;
    }

    public int getdIp() {
        return dIp;
    }

    public void setdIp(int dIp) {
        this.dIp = dIp;
    }

    public Long getDateLimit() {
        return dateLimit;
    }

    public void setDateLimit(Long dateLimit) {
        this.dateLimit = dateLimit;
    }
}
