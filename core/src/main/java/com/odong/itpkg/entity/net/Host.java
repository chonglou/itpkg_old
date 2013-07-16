package com.odong.itpkg.entity.net;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午10:58
 */
public class Host implements Serializable {
    private static final long serialVersionUID = -7362809864882645300L;
    private Long id;
    private Long company;
    private Long lan;
    private String netId;
    private String name;
    private String details;

    public String getNetId() {
        return netId;
    }

    public void setNetId(String netId) {
        this.netId = netId;
    }

    public Long getCompany() {
        return company;
    }

    public void setCompany(Long company) {
        this.company = company;
    }

    public Long getLan() {
        return lan;
    }

    public void setLan(Long lan) {
        this.lan = lan;
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
}
