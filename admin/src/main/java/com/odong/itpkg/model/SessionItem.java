package com.odong.itpkg.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 下午9:01
 */
public class SessionItem implements Serializable {
    private static final long serialVersionUID = 7022196605186543132L;
    private Long accountId;
    private String companyId;
    private String username;
    private String email;
    private Date created;
    private boolean admin;
    private boolean companyManager;

    public final static String KEY = "d1s7e0wp";

    public boolean isCompanyManager() {
        return companyManager;
    }

    public void setCompanyManager(boolean companyManager) {
        this.companyManager = companyManager;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

}
