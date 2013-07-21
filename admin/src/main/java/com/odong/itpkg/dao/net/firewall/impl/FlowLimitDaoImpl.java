package com.odong.itpkg.dao.net.firewall.impl;

import com.odong.itpkg.dao.net.firewall.FlowLimitDao;
import com.odong.itpkg.entity.net.firewall.FlowLimit;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-20
 * Time: 下午2:40
 */

@Repository("ff.flowLimitDao")
public class FlowLimitDaoImpl extends BaseJpa2DaoImpl<FlowLimit, Long> implements FlowLimitDao {
}
