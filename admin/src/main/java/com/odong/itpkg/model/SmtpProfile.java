package com.odong.itpkg.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-24
 * Time: 上午3:56
 */
public class SmtpProfile implements Serializable {
    public SmtpProfile(String host, String username, String password, String bcc) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.bcc = bcc;
        this.port = 25;
    }

    public SmtpProfile() {
    }

    private static final long serialVersionUID = -6614086445257490257L;
    private String host;
    private String username;
    private String password;
    private int port;
    private String bcc;
    private String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }
}
