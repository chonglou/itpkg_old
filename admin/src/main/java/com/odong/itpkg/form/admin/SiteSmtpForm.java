package com.odong.itpkg.form.admin;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午12:43
 */
public class SiteSmtpForm implements Serializable {
    private static final long serialVersionUID = 1230919592219488330L;
    @NotNull
    private String from;
    @Email
    @NotNull
    private String bcc;
    @NotNull
    private String host;

    @Min(1)
    @Max(65535)
    private int port;
    @NotNull
    @Size(min = 2, max = 20)
    private String username;
    @NotNull
    @Size(min = 6, max = 20)
    private String password;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
