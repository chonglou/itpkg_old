package com.odong.itpkg.form.admin;

import com.odong.itpkg.validation.IpV4;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午12:26
 */
public class SiteStateForm implements Serializable {
    private static final long serialVersionUID = 7267535797961055521L;
    @NotNull
    @IpV4
    private String ip;
    private boolean allowLogin;
    private boolean allowRegister;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

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
