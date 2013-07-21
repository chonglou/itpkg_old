package com.odong.itpkg.service;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 上午10:48
 */
public interface RbacService {
    boolean auth(long user, long company, OperationType type);

    void bind(long user, long company, OperationType type, boolean bind);

    void bindAdmin(long user, boolean bind);

    boolean auth(long user);

    public enum OperationType {
        MANAGER, USE
    }

}
