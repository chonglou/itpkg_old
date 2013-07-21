package com.odong.itpkg.dao.net.firewall.impl;

import com.odong.itpkg.dao.net.firewall.NatDao;
import com.odong.itpkg.entity.net.firewall.Nat;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-20
 * Time: 下午2:41
 */

@Repository("ff.natDao")
public class NatDaoImpl extends BaseJpa2DaoImpl<Nat, Long> implements NatDao {
}
