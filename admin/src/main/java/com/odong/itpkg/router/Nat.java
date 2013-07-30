package com.odong.itpkg.router;

import java.io.Serializable;

/**
 * 端口映射
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 上午9:57
 */
public class Nat implements Serializable {
    /**
     * @param address  内网地址
     * @param port     端口（保持一致）
     * @param protocol 协议
     */
    public Nat(String address, int port, Protocol protocol) {
        this.address = address;
        this.port = port;
        this.protocol = protocol;
    }

    private static final long serialVersionUID = 4731121804044374417L;
    public final String address;
    public final int port;
    public final Protocol protocol;

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Nat && (port == ((Nat) obj).port) && (protocol == ((Nat) obj).protocol));
    }

    @Override
    public int hashCode() {
        return 31 * port + protocol.hashCode();
    }
}
