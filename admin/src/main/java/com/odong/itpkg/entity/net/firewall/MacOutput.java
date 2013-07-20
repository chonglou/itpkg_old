package com.odong.itpkg.entity.net.firewall;

import com.odong.portal.entity.IdEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午10:27
 */
@Entity
@Table(name = "ffMacOut")
public class MacOutput extends IdEntity {
    private static final long serialVersionUID = -5876366263996455731L;
    @Column(nullable = false, updatable = false)
    private long host;
    @Column(nullable = false, updatable = false)
    private long mac;
    @Column(nullable = false, updatable = false)
    private long output;
    @Column(nullable = false, updatable = false)
    private Date created;

    public long getHost() {
        return host;
    }

    public void setHost(long host) {
        this.host = host;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public long getMac() {
        return mac;
    }

    public void setMac(long mac) {
        this.mac = mac;
    }

    public long getOutput() {
        return output;
    }

    public void setOutput(long output) {
        this.output = output;
    }
}
