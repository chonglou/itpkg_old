package com.odong.itpkg.entity.net;

import com.odong.itpkg.router.Type;
import com.odong.portal.entity.IdEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 上午9:26
 */
@Entity
@Table(name = "netMac")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Router extends IdEntity {
    private static final long serialVersionUID = 8425622395905007499L;
    @Column(nullable = false, updatable = false)
    private String company;
    @Column(nullable = false)
    private String url;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    private String details;
    @Column(nullable = false, updatable = false)
    private Date created;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
