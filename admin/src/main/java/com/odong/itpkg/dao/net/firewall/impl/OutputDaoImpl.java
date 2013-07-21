package com.odong.itpkg.dao.net.firewall.impl;

import com.odong.itpkg.dao.net.firewall.OutputDao;
import com.odong.itpkg.entity.net.firewall.Output;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-20
 * Time: 下午2:42
 */

@Repository("ff.outputDao")
public class OutputDaoImpl extends BaseJpa2DaoImpl<Output, Long> implements OutputDao {
}
