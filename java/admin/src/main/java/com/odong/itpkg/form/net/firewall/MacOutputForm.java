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
    private boolean bind;

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
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
