package com.odong.itpkg.form.net.firewall;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-2
 * Time: 下午10:03
 */
public class OutputForm implements Serializable {
    private static final long serialVersionUID = 8098483556369706221L;
    private Long id;
    @NotNull(message = "{val.notNull}")
    private String name;
    @NotNull(message = "{val.notNull}")
    private String key;
    private long dateLimit;

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getDateLimit() {
        return dateLimit;
    }

    public void setDateLimit(long dateLimit) {
        this.dateLimit = dateLimit;
    }
}
