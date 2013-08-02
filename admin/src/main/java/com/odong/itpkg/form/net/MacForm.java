package com.odong.itpkg.form.net;

import com.odong.itpkg.entity.net.Mac;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-2
 * Time: 下午2:57
 */
public class MacForm implements Serializable {
    private static final long serialVersionUID = -7051647145800331018L;
    private long user;
    private Mac.State state;

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public Mac.State getState() {
        return state;
    }

    public void setState(Mac.State state) {
        this.state = state;
    }
}
