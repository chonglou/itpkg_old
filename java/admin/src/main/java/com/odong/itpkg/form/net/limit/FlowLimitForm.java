package com.odong.itpkg.form.net.limit;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-27
 * Time: 下午6:25
 */
public class FlowLimitForm implements Serializable {
    private static final long serialVersionUID = -4558846010150909217L;
    private String name;
    private String details;
    private Integer upRate;
    private Integer downRate;
    private Integer upCeil;
    private Integer downCeil;
    private Long id;

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
}
