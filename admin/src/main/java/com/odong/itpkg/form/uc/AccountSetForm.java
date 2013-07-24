package com.odong.itpkg.form.uc;

import com.odong.itpkg.entity.uc.Account;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-21
 * Time: 下午5:02
 */
public class AccountSetForm implements Serializable {
    private static final long serialVersionUID = 3922033101638258165L;
    private long account;
    @NotNull
    private Account.State state;

    public long getAccount() {
        return account;
    }

    public void setAccount(long account) {
        this.account = account;
    }

    public Account.State getState() {
        return state;
    }

    public void setState(Account.State state) {
        this.state = state;
    }
}
