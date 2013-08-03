package com.odong.itpkg.form.net.firewall;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-2
 * Time: 下午10:47
 */
public class MacOutputForm implements Serializable {
    private static final long serialVersionUID = -3438456008396722991L;
    private long output;
    private long mac;
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public long getOutput() {
        return output;
    }

    public void setOutput(long output) {
        this.output = output;
    }

    public long getMac() {
        return mac;
    }

    public void setMac(long mac) {
        this.mac = mac;
    }
}
