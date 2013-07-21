package com.odong.itpkg.service;

import com.odong.itpkg.entity.uc.Company;
import com.odong.itpkg.entity.uc.Group;
import com.odong.itpkg.entity.uc.User;
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


    void addUser(String companyId, String email, String username, String password);

    List<User> listUser(String companyId);

    List<User> listUser(long groupId);

    User getUser(long userId);

    User getUser(String email);

    void setUserState(long userId, User.State state);

    void setUserPassword(long userId, String password);

    void setUserGroup(long userId, long groupId, boolean bind);

    void setUserInfo(long userId, String username, Contact contact);

    User auth(String email, String password);


}
