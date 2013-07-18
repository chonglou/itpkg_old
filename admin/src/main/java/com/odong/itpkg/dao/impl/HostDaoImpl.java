package com.odong.itpkg.dao.impl;

import com.odong.itpkg.dao.HostDao;
import com.odong.itpkg.entity.net.Host;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午11:18
 */
@Repository("net.hostDao")
public class HostDaoImpl extends BaseJpa2DaoImpl<Host,Long> implements HostDao {
}
