package com.odong.itpkg.form.uc;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-21
 * Time: 下午5:02
 */
public class AccountSetForm implements Serializable{
    private static final long serialVersionUID = 3922033101638258165L;
    private long account;
    private boolean enable;

    public long getAccount() {
        return account;
    }

    public void setAccount(long account) {
        this.account = account;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
