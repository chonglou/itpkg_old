package com.odong.itpkg.entity.net;

import com.odong.portal.entity.IdEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午10:58
 */

@Entity
@Table(name = "netHost")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Host extends IdEntity {
    public final static int KEY_LEN = 18;

    public enum State {
        RUNNING, SUBMIT, DONE
    }

    private static final long serialVersionUID = -7362809864882645300L;
    @Column(nullable = false, updatable = false)
    private String company;
    @Column(nullable = false, updatable = false)
    private String wanIp;
    @Column(nullable = false)
    private String lanNet;
    private boolean dmz;
    @Column
    private String dmzNet;
    @Column
    private String dmzMac;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String domain;
    @Lob
    private String details;
    private Date lastHeart;
    @Column(nullable = false)
    private String signKey;
    @Column(nullable = false)
    private int rpcPort;
    private boolean ping;
    @Column(nullable = false)
    private String wanMac;
    @Column(nullable = false)
    private String lanMac;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;
    @Column(nullable = false)
    private int space;
    @Column(nullable = false, updatable = false)
    private Date created;
    @Version
    private int version;

    public boolean isDmz() {
        return dmz;
    }

    public void setDmz(boolean dmz) {
        this.dmz = dmz;
    }

    public String getDmzNet() {
        return dmzNet;
    }

    public void setDmzNet(String dmzNet) {
        this.dmzNet = dmzNet;
    }

    public String getDmzMac() {
        return dmzMac;
    }

    public void setDmzMac(String dmzMac) {
        this.dmzMac = dmzMac;
    }

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getWanMac() {
        return wanMac;
    }

    public void setWanMac(String wanMac) {
        this.wanMac = wanMac;
    }

    public String getLanMac() {
        return lanMac;
    }

    public void setLanMac(String lanMac) {
        this.lanMac = lanMac;
    }

    public boolean isPing() {
        return ping;
    }

    public void setPing(boolean ping) {
        this.ping = ping;
    }

    public int getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(int rpcPort) {
        this.rpcPort = rpcPort;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Date getLastHeart() {
        return lastHeart;
    }

    public void setLastHeart(Date lastHeart) {
        this.lastHeart = lastHeart;
    }

    public String getSignKey() {
        return signKey;
    }

    public void setSignKey(String signKey) {
        this.signKey = signKey;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getWanIp() {
        return wanIp;
    }

    public void setWanIp(String wanIp) {
        this.wanIp = wanIp;
    }

    public String getLanNet() {
        return lanNet;
    }

    public void setLanNet(String lanNet) {
        this.lanNet = lanNet;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
