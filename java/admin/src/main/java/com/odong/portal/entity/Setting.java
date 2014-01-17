package com.odong.portal.entity;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-23
 * Time: 下午2:55
 */
@Entity
@Table(name = "siteEnv")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Setting implements Serializable {
    private static final long serialVersionUID = 2722519175265573466L;
    @Id
    @Column(nullable = false, updatable = false, unique = true)
    private String key;
    @Lob
    private String value;

    @Version
    private int version;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
