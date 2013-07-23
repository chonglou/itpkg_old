package com.odong.itpkg.service;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 上午10:48
 */
public interface RbacService {
    boolean authCompany(long account, String company, OperationType... types);

    void bindCompany(long account, String company, OperationType type, boolean bind);

    void bindAdmin(long account, boolean bind);

    boolean authAdmin(long account);

    public enum OperationType {
        MANAGE, USE
    }

}
