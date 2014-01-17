package com.odong.portal.dao.rbac.impl;

import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import com.odong.portal.dao.rbac.RoleDao;
import com.odong.portal.entity.rbac.Role;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-6-5
 * Time: 上午10:28
 */
@Repository("rbac.roleDao")
public class RoleDaoImpl extends BaseJpa2DaoImpl<Role, Long> implements RoleDao {
}
