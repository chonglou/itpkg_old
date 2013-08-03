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
public class InputAddForm implements Serializable {
    private static final long serialVersionUID = 5714817306639487529L;
    private Long id;
    @NotNull(message = "{val.notNull}")
    private String name;
    private Protocol protocol;
    private String sIp;
    @Port
    private int port;

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

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getsIp() {
        return sIp;
    }

    public void setsIp(String sIp) {
        this.sIp = sIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
