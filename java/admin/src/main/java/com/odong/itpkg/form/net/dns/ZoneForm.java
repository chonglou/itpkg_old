package com.odong.itpkg.form.net.dns;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-2
 * Time: 下午5:24
 */
public class ZoneForm implements Serializable {
    private static final long serialVersionUID = 8085626383612134210L;
    private Long id;
    @NotNull(message = "{val.notNull}")
    private String name;
    private String details;

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
