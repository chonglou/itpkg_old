package com.odong.itpkg.dao.net.impl;

import com.odong.itpkg.dao.net.MacDao;
import com.odong.itpkg.entity.net.Mac;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-20
 * Time: 下午2:43
 */

@Repository("net.macDao")
public class MacDaoImpl extends BaseJpa2DaoImpl<Mac, Long> implements MacDao {
}
