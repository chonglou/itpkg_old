package com.odong.itpkg.dao.net.dns.impl;

import com.odong.itpkg.dao.net.dns.ZoneDao;
import com.odong.itpkg.entity.net.dns.Zone;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-20
 * Time: 下午2:38
 */
@Repository("dns.zoneDao")
public class ZoneDaoImpl extends BaseJpa2DaoImpl<Zone,Long> implements ZoneDao{
}
