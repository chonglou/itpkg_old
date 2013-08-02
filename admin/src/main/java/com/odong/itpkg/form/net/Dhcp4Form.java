package com.odong.itpkg.form.net;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-2
 * Time: 下午3:14
 */
public class Dhcp4Form implements Serializable {
    private static final long serialVersionUID = 358802450631824749L;
    private boolean bind;
    private int ip;

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }

    public int getIp() {
        return ip;
    }

    public void setIp(int ip) {
        this.ip = ip;
    }
}
