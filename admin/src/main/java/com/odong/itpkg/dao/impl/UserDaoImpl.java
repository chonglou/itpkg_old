package com.odong.itpkg.dao.impl;

import com.odong.itpkg.dao.UserDao;
import com.odong.itpkg.entity.uc.User;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 上午10:59
 */
@Repository("uc.userDao")
public class UserDaoImpl extends BaseJpa2DaoImpl<User, Long> implements UserDao {
}
