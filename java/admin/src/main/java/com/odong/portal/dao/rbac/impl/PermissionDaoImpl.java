package com.odong.portal.dao.rbac.impl;

import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import com.odong.portal.dao.rbac.PermissionDao;
import com.odong.portal.entity.rbac.Permission;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-6-5
 * Time: 上午10:29
 */
@Repository("rbac.permissionDao")
public class PermissionDaoImpl extends BaseJpa2DaoImpl<Permission, Long> implements PermissionDao {
}
