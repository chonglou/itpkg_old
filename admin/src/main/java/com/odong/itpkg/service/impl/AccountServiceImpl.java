package com.odong.itpkg.service.impl;

import com.odong.itpkg.dao.uc.CompanyDao;
import com.odong.itpkg.dao.uc.UserDao;
import com.odong.itpkg.entity.uc.Company;
import com.odong.itpkg.entity.uc.User;
import com.odong.itpkg.model.Contact;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.itpkg.util.JsonHelper;
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
    public String addCompany(String name) {
        String id = UUID.randomUUID().toString();
        Company c = new Company();
        c.setId(id);
        c.setName(name);
        c.setCreated(new Date());
        companyDao.insert(c);
        return id;
    }

    @Override
    public List<User> listUser() {
        return userDao.list();
    }

    @Override
    public void setUserInfo(long id, String username, Contact contact) {
        User u = userDao.select(id);
        u.setUsername(username);
        u.setContact(jsonHelper.object2json(contact));
        userDao.update(u);
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
    public User getUser(long id) {
        return userDao.select(id);  //
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
    @Resource
    private JsonHelper jsonHelper;

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

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
