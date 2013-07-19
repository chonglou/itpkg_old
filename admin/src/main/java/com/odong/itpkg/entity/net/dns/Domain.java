package com.odong.itpkg.entity.net.dns;

import com.odong.portal.entity.IdEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午9:01
 */

@Entity
@Table(name = "dnsDomain")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Domain extends IdEntity {
    public enum Type{
        A,MX,NS
    }
    private static final long serialVersionUID = 1863549290229151142L;
    @Column(nullable = false)
    private Long zone;
    @Column(nullable = false)
    private String name;
    private Integer lanIp;
    private String wanIp;
    private int priority;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;
    private boolean local;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Long getZone() {
        return zone;
    }

    public void setZone(Long zone) {
        this.zone = zone;
    }

    public Integer getLanIp() {
        return lanIp;
    }

    public void setLanIp(Integer lanIp) {
        this.lanIp = lanIp;
    }

    public String getWanIp() {
        return wanIp;
    }

    public void setWanIp(String wanIp) {
        this.wanIp = wanIp;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }
}
