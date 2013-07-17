package com.odong.itpkg.entity.net.firewall;

import com.odong.portal.entity.IdEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
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
    private Long company;
    @Column(nullable = false)
    private String name;
    private Integer maxIn;
    private Integer maxOut;
    private Integer minIn;
    private Integer minOut;
    @Column(nullable = false, updatable = false)
    private Date created;

    public Long getCompany() {
        return company;
    }

    public void setCompany(Long company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxIn() {
        return maxIn;
    }

    public void setMaxIn(Integer maxIn) {
        this.maxIn = maxIn;
    }

    public Integer getMaxOut() {
        return maxOut;
    }

    public void setMaxOut(Integer maxOut) {
        this.maxOut = maxOut;
    }

    public Integer getMinIn() {
        return minIn;
    }

    public void setMinIn(Integer minIn) {
        this.minIn = minIn;
    }

    public Integer getMinOut() {
        return minOut;
    }

    public void setMinOut(Integer minOut) {
        this.minOut = minOut;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
