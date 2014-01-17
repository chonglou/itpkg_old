package com.odong.itpkg.dao.uc.impl;

import com.odong.itpkg.dao.uc.GroupUserDao;
import com.odong.itpkg.entity.uc.GroupUser;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-20
 * Time: 下午2:44
 */

@Repository("uc.groupUserDao")
public class GroupUserDaoImpl extends BaseJpa2DaoImpl<GroupUser, Long> implements GroupUserDao {
}
