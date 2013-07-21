package com.odong.itpkg.dao.net.firewall.impl;

import com.odong.itpkg.dao.net.firewall.DmzDao;
import com.odong.itpkg.entity.net.firewall.Dmz;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-20
 * Time: 下午2:39
 */

@Repository("ff.dmzDao")
public class DmzDaoImpl extends BaseJpa2DaoImpl<Dmz, Long> implements DmzDao {
}
