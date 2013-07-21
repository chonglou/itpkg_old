package com.odong.itpkg.dao.net.firewall.impl;

import com.odong.itpkg.dao.net.firewall.InputDao;
import com.odong.itpkg.entity.net.firewall.Input;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-20
 * Time: 下午2:40
 */

@Repository("ff.inputDao")
public class InputDaoImpl extends BaseJpa2DaoImpl<Input, Long> implements InputDao {
}
