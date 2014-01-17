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
@Table(name = "rbacPermission")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Permission extends IdEntity {
    private static final long serialVersionUID = 4521456318207452459L;

    @Column(nullable = false, updatable = false)
    private long role;
    @Column(nullable = false, updatable = false)
    private long resource;
    @Column(nullable = false, updatable = false)
    private long operation;
    @Column(nullable = false, updatable = false)
    private Date startUp;
    @Column(nullable = false, updatable = false)
    private Date shutDown;
    @Column(nullable = false, updatable = false)
    private Date created;


    public long getRole() {
        return role;
    }

    public void setRole(long role) {
        this.role = role;
    }

    public long getResource() {
        return resource;
    }

    public void setResource(long resource) {
        this.resource = resource;
    }

    public long getOperation() {
        return operation;
    }

    public void setOperation(long operation) {
        this.operation = operation;
    }

    public Date getStartUp() {
        return startUp;
    }

    public void setStartUp(Date startUp) {
        this.startUp = startUp;
    }

    public Date getShutDown() {
        return shutDown;
    }

    public void setShutDown(Date shutDown) {
        this.shutDown = shutDown;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
