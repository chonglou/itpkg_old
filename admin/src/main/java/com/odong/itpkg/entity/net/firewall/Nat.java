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
    private int port;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Protocol protocol;
    @Column(nullable = false)
    private Long mac;
    @Column(nullable = false)
    private Long dateLimit;
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
