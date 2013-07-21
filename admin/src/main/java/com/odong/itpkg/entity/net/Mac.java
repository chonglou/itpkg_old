package com.odong.itpkg.entity.net;

import com.odong.portal.entity.IdEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午10:55
 */
@Entity
@Table(name = "netMac")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Mac extends IdEntity {
    public enum State {
        ENABLE, SUBMIT, DISABLE
    }

    private static final long serialVersionUID = -885297753742159102L;

    @Column(nullable = false, updatable = false)
    private Long host;
    @Column(nullable = false, updatable = false)
    private String serial;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;
    @Column(nullable = false)
    private int ip;
    private Long user;
    private String hostname;
    private Long dateLimit;
    @Column(nullable = false)
    private long flowLimit;
    @Lob
    private String detail;
    private boolean bind;
    @Column(nullable = false, updatable = false)
    private Date created;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Long getDateLimit() {
        return dateLimit;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setDateLimit(Long dateLimit) {
        this.dateLimit = dateLimit;
    }

    public long getFlowLimit() {
        return flowLimit;
    }

    public void setFlowLimit(long flowLimit) {
        this.flowLimit = flowLimit;
    }

    public Long getHost() {
        return host;
    }

    public void setHost(Long host) {
        this.host = host;
    }


    public int getIp() {
        return ip;
    }

    public void setIp(int ip) {
        this.ip = ip;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
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
