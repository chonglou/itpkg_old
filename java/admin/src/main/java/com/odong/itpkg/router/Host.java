package com.odong.itpkg.router;

import java.io.Serializable;

/**
 * 上网设备信息
 * <p/>
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 上午9:50
 */
public class Host implements Serializable {
    private static final long serialVersionUID = 6996403100285435624L;

    /**
     * @param name 主机名
     * @param mac  主机mac
     * @param ip   主机ip
     */
    public Host(String name, String mac, String ip) {
        this.name = name;
        this.mac = mac;
        this.ip = ip;
    }

    public final String name;
    public final String mac;
    public final String ip;

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Host && mac.equals(((Host) obj).mac));
    }

    @Override
    public int hashCode() {
        return mac.hashCode();
    }
}
