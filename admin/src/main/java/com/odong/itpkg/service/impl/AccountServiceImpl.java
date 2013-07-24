package com.odong.itpkg.service.impl;

import com.odong.itpkg.dao.net.MacDao;
import com.odong.itpkg.dao.uc.*;
import com.odong.itpkg.entity.uc.*;
import com.odong.itpkg.model.Contact;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.itpkg.util.JsonHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 上午10:49
 */

@Service("accountService")
public class AccountServiceImpl implements AccountService {


    @Override
    public void addGroup(String companyId, String name, String details) {
        Group g = new Group();
        g.setCompany(companyId);
        g.setName(name);
        g.setDetails(details);
        g.setCreated(new Date());
        groupDao.insert(g);
    }

    @Override
    public void setGroup(long groupId, String name, String details) {
        Group g = groupDao.select(groupId);
        g.setName(name);
        g.setDetails(details);
        groupDao.update(g);
    }

    @Override
    public List<Group> listGroup(String companyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("company", companyId);
        return groupDao.list("SELECT i FROM Group i WHERE i.company=:company)", map);  //
    }

    @Override
    public List<Group> listGroup(long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", userId);
        return groupDao.list("SELECT i FROM Group g WHERE g.id in (SELECT gu.group FROM GroupUser gu WHERE gu.user=:user)", map);  //

    }

    @Override
    public Group getGroup(long groupId) {
        return groupDao.select(groupId);  //
    }

    @Override
    public void delGroup(long groupId) {
        Map<String, Object> map = new HashMap<>();
        map.put("group", groupId);
        groupUserDao.delete("DELETE GroupUser i WHERE i.group=:group", map);
        groupDao.delete(groupId);
    }

    @Override
    public Company getCompany(String companyId) {
        return companyDao.select(companyId);  //
    }

    @Override
    public List<Company> listCompany() {
        return companyDao.list();  //
    }

    @Override
    public void addCompany(String id, String name, String details) {
        Company c = new Company();
        c.setId(id);
        c.setName(name);
        c.setDetails(details);
        c.setCreated(new Date());
        c.setState(Company.State.SUBMIT);
        companyDao.insert(c);
    }

    @Override
    public void setCompanyInfo(String companyId, String name, String details) {
        Company c = companyDao.select(companyId);
        c.setName(name);
        c.setDetails(details);
        companyDao.update(c);
    }

    @Override
    public void setCompanyState(String companyId, Company.State state) {
        Company c = companyDao.select(companyId);
        c.setState(state);
        companyDao.update(c);
    }

    @Override
    public void addAccount(String companyId, String email, String username, String password) {
        Account u = new Account();
        u.setEmail(email);
        u.setUsername(username);
        u.setPassword(encryptHelper.encrypt(password));
        u.setCreated(new Date());
        u.setCompany(companyId);
        u.setContact(jsonHelper.object2json(new Contact()));
        u.setState(Account.State.SUBMIT);
        accountDao.insert(u);
    }

    @Override
    public List<Account> listAccount(String companyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("company", companyId);
        return accountDao.list("SELECT i FROM Account i WHERE i.company=:company)", map);  //
    }

    @Override
    public List<GroupUser> listGroupUser(String companyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("company", companyId);
        return groupUserDao.list("SELECT gu FROM GroupUser gu WHERE gu.group IN (SELECT g.group FROM Group g WHERE g.company=:company)", map);  //
    }

    @Override
    public List<User> listUserByGroup(long groupId) {
        Map<String, Object> map = new HashMap<>();
        map.put("group", groupId);
        return userDao.list("SELECT u FROM User u WHERE u.id in (SELECT gu.user FROM GroupUser gu WHERE gu.group=:group)", map);  //
    }

    @Override
    public List<User> listUserByCompany(String companyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("company", companyId);
        return userDao.list("SELECT i FROM User i WHERE i.company=:company", map);  //

    }

    @Override
    public void addUser(String username, String unit, Contact contact, String company) {
        User user = new User();
        user.setCreated(new Date());
        user.setCompany(company);
        user.setUsername(username);
        user.setUnit(unit);
        user.setContact(jsonHelper.object2json(contact));
        userDao.insert(user);
    }

    @Override
    public void delUser(long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", userId);
        groupUserDao.delete("DELETE GroupUser i WHERE i.user=:user", map);
        macDao.update("UPDATE Mac i SET i.user=NULL WHERE i.user=:user", map);
        userDao.delete(userId);
    }

    @Override
    public User getUser(long userId) {
        return userDao.select(userId);
    }

    @Override
    public User getUser(String username, String company) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("company", company);
        return userDao.select("SELECT i FROM User i WHERE i.username=:username && i.company=:company", map);
    }

    @Override
    public List<Account> listAccount() {
        return accountDao.list();
    }

    @Override
    public Account getAccount(long accountId) {
        return accountDao.select(accountId);  //
    }

    @Override
    public Account getAccount(String email) {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        return accountDao.select("SELECT i FROM Account i WHERE i.email=:email", map);
    }

    @Override
    public void setAccountState(long accountId, Account.State state) {
        Account account = accountDao.select(accountId);
        account.setState(state);
        accountDao.update(account);
    }

    @Override
    public void setAccountLastLogin(long accountId) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", accountId);
        map.put("lastLogin", new Date());
        accountDao.update("UPDATE Account i SET i.lastLogin=:lastLogin WHERE i.id=:id", map);
    }

    @Override
    public void setAccountPassword(long accountId, String password) {
        Account account = accountDao.select(accountId);
        account.setPassword(encryptHelper.encrypt(password));
        accountDao.update(account);
    }

    @Override
    public void setUserGroup(long userId, long groupId, boolean bind) {
        if (bind) {
            GroupUser gu = new GroupUser();
            gu.setUser(userId);
            gu.setGroup(groupId);
            gu.setCreated(new Date());
            groupUserDao.insert(gu);

        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("user", userId);
            map.put("group", groupId);
            groupUserDao.delete("DELETE GroupUser i WHERE i.user=:user && i.group=:group", map);
        }
    }

    @Override
    public void setUserInfo(long userId, String username, String unit, Contact contact) {
        User u = userDao.select(userId);
        u.setUsername(username);
        u.setUnit(unit);
        u.setContact(jsonHelper.object2json(contact));
        userDao.update(u);
    }

    @Override
    public GroupUser getGroupUser(long groupId, long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", userId);
        map.put("group", groupId);
        return groupUserDao.select("SELECT i FROM GroupUser i WHERE i.user=:user && i.group=:group", map);
    }

    @Override
    public void setAccountInfo(long accountId, String username, Contact contact) {
        Account account = accountDao.select(accountId);
        account.setUsername(username);
        account.setContact(jsonHelper.object2json(contact));
        accountDao.update(account);
    }

    @Override
    public Account auth(String email, String password) {
        Account account = getAccount(email);
        return account != null && encryptHelper.check(password, account.getPassword()) ? account : null;  //
    }

    @Resource
    private UserDao userDao;
    @Resource
    private GroupDao groupDao;
    @Resource
    private GroupUserDao groupUserDao;
    @Resource
    private EncryptHelper encryptHelper;
    @Resource
    private AccountDao accountDao;
    @Resource
    private CompanyDao companyDao;
    @Resource
    private JsonHelper jsonHelper;
    @Resource
    private MacDao macDao;

    public void setMacDao(MacDao macDao) {
        this.macDao = macDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    public void setGroupUserDao(GroupUserDao groupUserDao) {
        this.groupUserDao = groupUserDao;
    }

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public void setCompanyDao(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

}
