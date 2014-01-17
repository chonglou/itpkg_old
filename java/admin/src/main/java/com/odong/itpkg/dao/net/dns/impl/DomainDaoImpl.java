package com.odong.itpkg.dao.net.dns.impl;

import com.odong.itpkg.dao.net.dns.DomainDao;
import com.odong.itpkg.entity.net.dns.Domain;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-20
 * Time: 下午2:37
 */
@Repository("dns.domainDao")
public class DomainDaoImpl extends BaseJpa2DaoImpl<Domain, Long> implements DomainDao {
}
