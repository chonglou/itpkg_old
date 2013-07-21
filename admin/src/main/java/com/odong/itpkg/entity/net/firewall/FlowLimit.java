package com.odong.itpkg.entity.net.firewall;

import com.odong.portal.entity.IdEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午11:24
 */

@Entity
@Table(name = "ffFlow")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FlowLimit extends IdEntity {
    private static final long serialVersionUID = -4311479941247650306L;
    @Column(nullable = false, updatable = false)
    private String company;
    @Column(nullable = false)
    private String name;
    @Lob
    private String details;
    private Integer upRate;
    private Integer downRate;
    private Integer upCeil;
    private Integer downCeil;
    @Column(nullable = false, updatable = false)
    private Date created;
    @Version
    private int version;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUpRate() {
        return upRate;
    }

    public void setUpRate(Integer upRate) {
        this.upRate = upRate;
    }

    public Integer getDownRate() {
        return downRate;
    }

    public void setDownRate(Integer downRate) {
        this.downRate = downRate;
    }

    public Integer getUpCeil() {
        return upCeil;
    }

    public void setUpCeil(Integer upCeil) {
        this.upCeil = upCeil;
    }

    public Integer getDownCeil() {
        return downCeil;
    }

    public void setDownCeil(Integer downCeil) {
        this.downCeil = downCeil;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
