package com.odong.itpkg.dao.net.firewall.impl;

import com.odong.itpkg.dao.net.firewall.MacOutputDao;
import com.odong.itpkg.entity.net.firewall.MacOutput;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-20
 * Time: 下午2:41
 */

@Repository("ff.macOutputDao")
public class MacOutputDaoImpl extends BaseJpa2DaoImpl<MacOutput, Long> implements MacOutputDao {
}
