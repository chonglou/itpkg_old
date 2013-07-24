package com.odong.itpkg.form.personal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午1:03
 */
public class SetPwdForm implements Serializable {
    private static final long serialVersionUID = 1266968470855323112L;
    @NotNull
    @Size(min = 6, max = 20)
    private String oldPwd;
    @NotNull
    @Size(min = 6, max = 20)
    private String newPwd;
    @NotNull
    private String rePwd;

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }

    public String getRePwd() {
        return rePwd;
    }

    public void setRePwd(String rePwd) {
        this.rePwd = rePwd;
    }
}
