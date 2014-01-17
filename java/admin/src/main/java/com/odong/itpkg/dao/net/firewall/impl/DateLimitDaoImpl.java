package com.odong.itpkg.dao.net.firewall.impl;

import com.odong.itpkg.dao.net.firewall.DateLimitDao;
import com.odong.itpkg.entity.net.firewall.DateLimit;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-20
 * Time: 下午2:38
 */

@Repository("ff.dateLimitDao")
public class DateLimitDaoImpl extends BaseJpa2DaoImpl<DateLimit, Long> implements DateLimitDao {
}
