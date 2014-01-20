package com.odong.portal.entity.rbac;

import com.odong.portal.entity.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-6-5
 * Time: 上午10:21
 */
@Entity
@Table(name = "rbacResource")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Resource extends IdEntity {
    private static final long serialVersionUID = -2844544742200915947L;

    @Column(nullable = false, unique = true, updatable = false)
    private String name;
    @Column(nullable = false, updatable = false)
    private Date created;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}