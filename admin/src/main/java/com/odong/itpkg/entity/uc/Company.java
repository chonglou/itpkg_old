package com.odong.itpkg.entity.uc;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午11:02
 */

@Entity
@Table(name = "ucCompany")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Company implements Serializable {
    public enum State {
        ENABLE, DISABLE
    }

    private static final long serialVersionUID = 9041368825509992267L;

    @Id
    @Column(nullable = false, updatable = false, unique = true)
    private String id;
    @Column(nullable = false)
    private String name;
    @Lob
    private String details;
    @Column(nullable = false, updatable = false)
    private Date created;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
