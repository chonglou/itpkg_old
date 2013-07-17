package com.odong.itpkg.service.impl;

import com.odong.itpkg.dao.CompanyDao;
import com.odong.itpkg.dao.UserDao;
import com.odong.itpkg.entity.uc.Company;
import com.odong.itpkg.entity.uc.User;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.util.EncryptHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 上午10:49
 */

@Service("accountService")
public class AccountServiceImpl implements AccountService {
    @Override
    public String addCompany(String name, String details) {
        String id = UUID.randomUUID().toString();
        Company c = new Company();
        c.setId(id);
        c.setName(name);
        c.setDetails(details);
        c.setCreated(new Date());
        companyDao.insert(c);
        return id;
    }

    @Override
    public List<User> listUser() {
        return userDao.list();
    }

    @Override
    public void addUser(String email, String username, String password, String company) {
        User u = new User();
        u.setEmail(email);
        u.setUsername(username);
        u.setPassword(encryptHelper.encrypt(password));
        u.setCreated(new Date());
        u.setCompany(company);
        u.setState(User.State.ENABLE);
        userDao.insert(u);
    }

    @Override
    public User getUser(String email) {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        return userDao.select("FROM User as i WHERE i.email=:email", map);
    }

    @Override
    public User auth(String email, String password) {
        User u = getUser(email);
        return u != null && encryptHelper.check(password, u.getPassword()) ? u : null;  //
    }

    @Resource
    private EncryptHelper encryptHelper;
    @Resource
    private UserDao userDao;
    @Resource
    private CompanyDao companyDao;

    public void setCompanyDao(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
