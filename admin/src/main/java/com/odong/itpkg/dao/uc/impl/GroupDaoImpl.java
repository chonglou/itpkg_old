package com.odong.itpkg.dao.uc.impl;

import com.odong.itpkg.dao.uc.GroupDao;
import com.odong.itpkg.entity.uc.Group;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-20
 * Time: 下午2:43
 */

@Repository("uc.groupDao")
public class GroupDaoImpl extends BaseJpa2DaoImpl<Group,Long> implements GroupDao {
}
