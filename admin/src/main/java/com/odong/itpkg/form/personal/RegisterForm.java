package com.odong.itpkg.form.personal;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午11:44
 */
public class RegisterForm implements Serializable {
    private static final long serialVersionUID = 2172735998888599738L;
    @NotNull
    @Size(min = 2, max = 20)
    private String company;
    @Email
    @NotNull
    private String email;
    @NotNull
    @Size(min = 2, max = 20)
    private String username;
    @NotNull
    @Size(min = 6, max = 20)
    private String newPwd;
    @NotNull
    @Size(min = 6, max = 20)
    private String rePwd;
    @NotNull
    private String captcha;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
