package com.odong.itpkg.form.personal;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午10:49
 */
public class LoginForm implements Serializable {
    private static final long serialVersionUID = -4177551343660318353L;
    @Email
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String captcha;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
