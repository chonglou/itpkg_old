package com.odong.itpkg.form.net;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-21
 * Time: 下午5:10
 */
public class DomainForm implements Serializable {
    private static final long serialVersionUID = -4258150763062874208L;
    private Long zone;
    @NotNull
    private String name;
    private String details;

    public Long getZone() {
        return zone;
    }

    public void setZone(Long zone) {
        this.zone = zone;
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
