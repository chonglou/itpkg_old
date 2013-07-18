package com.odong.itpkg.form.admin;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午12:26
 */
public class SiteStateForm implements Serializable {
    private static final long serialVersionUID = 7267535797961055521L;
    private boolean allowLogin;
    private boolean allowRegister;

    public boolean isAllowLogin() {
        return allowLogin;
    }

    public void setAllowLogin(boolean allowLogin) {
        this.allowLogin = allowLogin;
    }

    public boolean isAllowRegister() {
        return allowRegister;
    }

    public void setAllowRegister(boolean allowRegister) {
        this.allowRegister = allowRegister;
    }
}
