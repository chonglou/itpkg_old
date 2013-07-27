package com.odong.itpkg.form.net;

import com.odong.itpkg.validation.IpV4;
import com.odong.itpkg.validation.Mac;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-26
 * Time: 上午9:56
 */
public class HostLanForm implements Serializable {
    private static final long serialVersionUID = 4269287883307140035L;
    private int id;
    @NotNull
    @Mac
    private String lanMac;
    @NotNull
    @IpV4
    private String lanNet;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanMac() {
        return lanMac;
    }

    public void setLanMac(String lanMac) {
        this.lanMac = lanMac;
    }

    public String getLanNet() {
        return lanNet;
    }

    public void setLanNet(String lanNet) {
        this.lanNet = lanNet;
    }
}
