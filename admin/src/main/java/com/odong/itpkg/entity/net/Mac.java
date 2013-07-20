package com.odong.itpkg.entity.net;

import com.odong.portal.entity.IdEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

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
    public enum State{
        ENABLE,DISABLE
    }
    private static final long serialVersionUID = -885297753742159102L;

    @Column(nullable = false, updatable = false)
    private Long host;
    @Column(nullable = false, updatable = false)
    private String serial;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;
    private int ip;
    private String hostname;
    private Long user;
    private Long dateLimit;
    @Column(nullable = false)
    private Long flowLimit;
    private boolean bind;

    public Long getDateLimit() {
        return dateLimit;
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
