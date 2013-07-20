package com.odong.itpkg.dao.net.impl;

import com.odong.itpkg.dao.net.IpDao;
import com.odong.itpkg.entity.net.Ip;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午11:18
 */
@Repository("net.ipDao")
public class IpDaoImpl extends BaseJpa2DaoImpl<Ip, Long> implements IpDao {
}
