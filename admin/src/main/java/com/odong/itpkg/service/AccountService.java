package com.odong.itpkg.service;

import com.odong.itpkg.entity.uc.User;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午10:50
 */
public interface AccountService {
    String addCompany(String name, String details);

    List<User> listUser();

    void addUser(String email, String username, String password, String company);

    User getUser(String email);

    User auth(String email, String password);
}
