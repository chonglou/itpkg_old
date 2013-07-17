package com.odong.itpkg.entity.uc;

import com.odong.portal.entity.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午11:00
 */

@Entity
@Table(name = "ucLog")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Log extends IdEntity {
    public enum Type {
        ERROR, DEBUG, INFO, WARN
    }

    private static final long serialVersionUID = -3548662034390764951L;
    @Column(nullable = false, updatable = false)
    private Long user;
    @Lob
    @Column(nullable = false, updatable = false)
    private String message;
    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
