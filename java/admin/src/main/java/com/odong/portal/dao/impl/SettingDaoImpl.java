package com.odong.portal.dao.impl;

import com.odong.portal.dao.SettingDao;
import com.odong.portal.entity.Setting;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-23
 * Time: 下午3:43
 */
@Repository("settingDao")
public class SettingDaoImpl extends BaseJpa2DaoImpl<Setting, String> implements SettingDao {

}
