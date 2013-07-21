package com.odong.itpkg.entity.net.dns;

import com.odong.portal.entity.IdEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午9:03
 */
@Entity
@Table(name = "dnsZone")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Zone extends IdEntity {
    private static final long serialVersionUID = 8278817293761075567L;
    @Column(nullable = false, updatable = false)
    private long host;
    @Column(nullable = false, updatable = false)
    private String name;
    @Lob
    private String details;
    @Column(nullable = false, updatable = false)
    private Date created;

    public long getHost() {
        return host;
    }

    public void setHost(long host) {
        this.host = host;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

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

}
