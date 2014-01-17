package com.odong.itpkg.dao.uc.impl;

import com.odong.itpkg.dao.uc.UserDao;
import com.odong.itpkg.entity.uc.User;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-21
 * Time: 下午3:25
 */
@Repository("uc.userDao")
public class UserDaoImpl extends BaseJpa2DaoImpl<User, Long> implements UserDao {
}
