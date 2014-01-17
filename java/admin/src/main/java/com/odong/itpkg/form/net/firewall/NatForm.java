package com.odong.itpkg.form.net.firewall;

import com.odong.itpkg.entity.net.firewall.Protocol;
import com.odong.itpkg.validation.Port;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-2
 * Time: 下午10:03
 */
public class NatForm implements Serializable {
    private static final long serialVersionUID = 4953995747412379373L;
    private Long id;
    @NotNull(message = "{val.notNull}")
    private String name;
    @Port
    private int sPort;
    @Port
    private int dPort;
    private int dIp;
    private Protocol protocol;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getsPort() {
        return sPort;
    }

    public void setsPort(int sPort) {
        this.sPort = sPort;
    }

    public int getdPort() {
        return dPort;
    }

    public void setdPort(int dPort) {
        this.dPort = dPort;
    }

    public int getdIp() {
        return dIp;
    }

    public void setdIp(int dIp) {
        this.dIp = dIp;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }
}
