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
    private Long ssAccountId;
    private String ssCompanyId;
    private String ssUsername;
    private String ssEmail;
    private Date ssCreated;
    private boolean ssAdmin;
    private boolean ssCompanyManager;

    public final static String KEY = "d1s7e0wp";


    public Long getSsAccountId() {
        return ssAccountId;
    }

    public void setSsAccountId(Long ssAccountId) {
        this.ssAccountId = ssAccountId;
    }

    public String getSsCompanyId() {
        return ssCompanyId;
    }

    public void setSsCompanyId(String ssCompanyId) {
        this.ssCompanyId = ssCompanyId;
    }

    public String getSsUsername() {
        return ssUsername;
    }

    public void setSsUsername(String ssUsername) {
        this.ssUsername = ssUsername;
    }

    public String getSsEmail() {
        return ssEmail;
    }

    public void setSsEmail(String ssEmail) {
        this.ssEmail = ssEmail;
    }

    public Date getSsCreated() {
        return ssCreated;
    }

    public void setSsCreated(Date ssCreated) {
        this.ssCreated = ssCreated;
    }

    public boolean isSsAdmin() {
        return ssAdmin;
    }

    public void setSsAdmin(boolean ssAdmin) {
        this.ssAdmin = ssAdmin;
    }

    public boolean isSsCompanyManager() {
        return ssCompanyManager;
    }

    public void setSsCompanyManager(boolean ssCompanyManager) {
        this.ssCompanyManager = ssCompanyManager;
    }
}
