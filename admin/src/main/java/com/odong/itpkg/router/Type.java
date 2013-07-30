package com.odong.itpkg.router;

/**
 * 路由器型号
 * <p/>
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 上午10:19
 */
public enum Type {
    TPLINK_WR841N("TP-Link WR841N");

    private Type(String value) {
        this.value = value;
    }

    private final String value;


    @Override
    public String toString() {
        return value;
    }
}
