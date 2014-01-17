package com.odong.itpkg.service;

import com.odong.itpkg.entity.uc.*;
import com.odong.itpkg.model.Contact;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午10:50
 */
public interface AccountService {


    void addGroup(String companyId, String name, String details);

    void setGroup(long groupId, String name, String details);

    List<Group> listGroup(String companyId);

    List<Group> listGroup(long userId);

    Group getGroup(long groupId);

    void delGroup(long groupId);


    Company getCompany(String companyId);

    List<Company> listCompany();

    void addCompany(String id, String name, String details);

    void setCompanyInfo(String companyId, String name, String details);

    void setCompanyState(String companyId, Company.State state);


    void addAccount(String companyId, String email, String username, String password);

    List<Account> listAccount(String companyId);

    List<GroupUser> listGroupUser(String companyId);

    List<User> listUserByGroup(long groupId);

    List<User> listUserByCompany(String companyId);

    void addUser(String username, String unit, Contact contact, String company);

    void delUser(long userId);

    User getUser(long userId);

    User getUser(String username, String company);

    void setUserGroup(long userId, long groupId, boolean bind);

    void setUserInfo(long userId, String username, String unit, Contact contact);

    List<Account> listAccount();

    Account getAccount(long accountId);

    Account getAccount(String email);

    void setAccountState(long accountId, Account.State state);

    void setAccountLastLogin(long accountId);

    void setAccountPassword(long accountId, String password);

    void delAccount(long accountId);

    GroupUser getGroupUser(long groupId, long userId);

    void setAccountInfo(long accountId, String username, Contact contact);

    Account auth(String email, String password);


}
