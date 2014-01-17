package com.odong.itpkg.form.net.dns;

import com.odong.itpkg.entity.net.dns.Domain;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-21
 * Time: 下午5:10
 */
public class DomainForm implements Serializable {
    private static final long serialVersionUID = -4258150763062874208L;
    private Long zone;
    @NotNull
    private String name;
    private Domain.Type type;
    private int lanIp;
    private String wanIp;
    private int priority;
    private boolean local;

    public Long getZone() {
        return zone;
    }

    public void setZone(Long zone) {
        this.zone = zone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Domain.Type getType() {
        return type;
    }

    public void setType(Domain.Type type) {
        this.type = type;
    }

    public int getLanIp() {
        return lanIp;
    }

    public void setLanIp(int lanIp) {
        this.lanIp = lanIp;
    }

    public String getWanIp() {
        return wanIp;
    }

    public void setWanIp(String wanIp) {
        this.wanIp = wanIp;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }
}
