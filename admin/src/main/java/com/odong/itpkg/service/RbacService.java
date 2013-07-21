package com.odong.itpkg.service;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 上午10:48
 */
public interface RbacService {
    boolean authCompany(long user, long company, OperationType type);

    void bindCompany(long user, long company, OperationType type, boolean bind);

    void bindAdmin(long user, boolean bind);

    boolean authAdmin(long user);

    public enum OperationType {
        MANAGER, USE
    }

}
