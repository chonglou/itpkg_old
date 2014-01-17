package com.odong.itpkg.form.admin;

import com.odong.itpkg.entity.uc.Company;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-24
 * Time: 下午12:53
 */
public class CompanyStateForm implements Serializable {
    private static final long serialVersionUID = -1751368623835587853L;
    @NotNull
    private String company;
    @NotNull
    private Company.State state;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Company.State getState() {
        return state;
    }

    public void setState(Company.State state) {
        this.state = state;
    }
}
