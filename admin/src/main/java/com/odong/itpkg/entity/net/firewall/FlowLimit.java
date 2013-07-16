package com.odong.itpkg.entity.net.firewall;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午11:24
 */
public class FlowLimit implements Serializable {
    private static final long serialVersionUID = -4311479941247650306L;
    private Long id;
    private Long company;
    private String name;
    private String details;
    private int maxIn;
    private int maxOut;
    private int minIn;
    private int minOut;
    private Date created;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getCompany() {
        return company;
    }

    public void setCompany(Long company) {
        this.company = company;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getMaxIn() {
        return maxIn;
    }

    public void setMaxIn(int maxIn) {
        this.maxIn = maxIn;
    }

    public int getMaxOut() {
        return maxOut;
    }

    public void setMaxOut(int maxOut) {
        this.maxOut = maxOut;
    }

    public int getMinIn() {
        return minIn;
    }

    public void setMinIn(int minIn) {
        this.minIn = minIn;
    }

    public int getMinOut() {
        return minOut;
    }

    public void setMinOut(int minOut) {
        this.minOut = minOut;
    }
}
