package com.odong.itpkg.service.impl;

import com.odong.itpkg.dao.uc.CompanyDao;
import com.odong.itpkg.dao.uc.GroupDao;
import com.odong.itpkg.dao.uc.GroupUserDao;
import com.odong.itpkg.dao.uc.UserDao;
import com.odong.itpkg.dao.uc.impl.GroupUserDaoImpl;
import com.odong.itpkg.entity.uc.Company;
import com.odong.itpkg.entity.uc.Group;
import com.odong.itpkg.entity.uc.GroupUser;
import com.odong.itpkg.entity.uc.User;
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
        return groupDao.list("FROM Group AS i WHERE i.company=:company)", map);  //
    }

    @Override
    public List<Group> listGroup(long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", userId);
        return groupDao.list("FROM Group AS g WHERE g.id in (SELECT gu.group FROM GroupUser AS gu WHERE gu.user=:user)", map);  //

    }

    @Override
    public Group getGroup(long groupId) {
        return groupDao.select(groupId);  //
    }

    @Override
    public void delGroup(long groupId) {
        Map<String, Object> map = new HashMap<>();
        map.put("group", groupId);
        groupUserDao.delete("DELETE GroupUser AS i WHERE i.group=:group", map);
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
    public void addUser(String companyId, String email, String username, String password) {
        User u = new User();
        u.setEmail(email);
        u.setUsername(username);
        u.setPassword(encryptHelper.encrypt(password));
        u.setCreated(new Date());
        u.setCompany(companyId);
        u.setState(User.State.SUBMIT);
        userDao.insert(u);
    }

    @Override
    public List<User> listUser(String companyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("company", companyId);
        return userDao.list("FROM USER AS u WHERE u.company=:company)", map);  //
    }

    @Override
    public List<GroupUser> listGroupUser(String companyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("company", companyId);
        return groupUserDao.list("FROM GroupUser AS gu WHERE gu.group IN (SELECT Company AS c WHERE c.id=:company)", map);  //
    }

    @Override
    public List<User> listUser(long groupId) {
        Map<String, Object> map = new HashMap<>();
        map.put("group", groupId);
        return userDao.list("FROM USER AS u WHERE u.id in (SELECT gu.user FROM GroupUser AS gu WHERE gu.group=:group)", map);  //
    }

    @Override
    public List<User> listUser() {
        return userDao.list();
    }

    @Override
    public User getUser(long userId) {
        return userDao.select(userId);  //
    }

    @Override
    public User getUser(String email) {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        return userDao.select("FROM User as i WHERE i.email=:email", map);
    }

    @Override
    public void setUserState(long userId, User.State state) {
        User user = userDao.select(userId);
        user.setState(state);
        userDao.update(user);
    }

    @Override
    public void setUserPassword(long userId, String password) {
        User user = userDao.select(userId);
        user.setPassword(encryptHelper.encrypt(password));
        userDao.update(user);
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
            groupUserDao.delete("DELETE GroupUser AS i WHERE i.user=:user && i.group=:group", map);
        }
    }

    @Override
    public GroupUser getGroupUser(long groupId, long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", userId);
        map.put("group", groupId);
        return  groupUserDao.select("SELECT GroupUser AS i WHERE i.user=:user && i.group=:group", map);
    }

    @Override
    public void setUserInfo(long userId, String username, Contact contact) {
        User u = userDao.select(userId);
        u.setUsername(username);
        u.setContact(jsonHelper.object2json(contact));
        userDao.update(u);
    }

    @Override
    public User auth(String email, String password) {
        User u = getUser(email);
        return u != null && encryptHelper.check(password, u.getPassword()) ? u : null;  //
    }

    @Resource
    private GroupDao groupDao;
    @Resource
    private GroupUserDao groupUserDao;
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
