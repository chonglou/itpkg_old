package com.odong.itpkg.form.net.limit;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-2
 * Time: 下午9:35
 */
public class BindForm implements Serializable {
    private static final long serialVersionUID = -6623824818007341544L;
    private long flLimit;
    private long mac;

    public long getFlLimit() {
        return flLimit;
    }

    public void setFlLimit(long flLimit) {
        this.flLimit = flLimit;
    }

    public long getMac() {
        return mac;
    }

    public void setMac(long mac) {
        this.mac = mac;
    }
}
