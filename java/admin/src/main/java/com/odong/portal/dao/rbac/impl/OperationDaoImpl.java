package com.odong.portal.dao.rbac.impl;

import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import com.odong.portal.dao.rbac.OperationDao;
import com.odong.portal.entity.rbac.Operation;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-6-5
 * Time: 上午10:29
 */
@Repository("rbac.operationDao")
public class OperationDaoImpl extends BaseJpa2DaoImpl<Operation, Long> implements OperationDao {
}
