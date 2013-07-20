package com.odong.itpkg.dao.uc.impl;

import com.odong.itpkg.dao.uc.LogDao;
import com.odong.itpkg.entity.uc.Log;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 上午11:00
 */
@Repository("uc.logDao")
public class LogDaoImpl extends BaseJpa2DaoImpl<Log, Long> implements LogDao {
}
