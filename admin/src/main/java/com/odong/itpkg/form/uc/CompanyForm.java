package com.odong.itpkg.form.uc;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-21
 * Time: 上午11:28
 */
public class CompanyForm implements Serializable {
    private static final long serialVersionUID = -6891945637778527981L;
    @NotNull
    @Size(min = 2, max = 20)
    private String name;
    private String details;

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
