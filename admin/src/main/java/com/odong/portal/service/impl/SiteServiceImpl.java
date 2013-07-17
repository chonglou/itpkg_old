package com.odong.portal.service.impl;

import com.odong.itpkg.util.JsonHelper;
import com.odong.portal.dao.SettingDao;
import com.odong.portal.entity.Setting;
import com.odong.portal.service.SiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-22
 * Time: 下午2:29
 */
@Service("siteService")
public class SiteServiceImpl implements SiteService {


    @Override
    public Boolean getBoolean(String key) {
        return getObject(key, Boolean.class);
    }

    @Override
    public Date getDate(String key) {
        return getObject(key, Date.class);  //
    }

    @Override
    public Long getLong(String key) {
        return getObject(key, Long.class);  //
    }

    @Override
    public Integer getInteger(String key) {
        return getObject(key, Integer.class);  //
    }

    @Override
    public String getString(String key) {
        return getObject(key, String.class);
    }

    @Override
    public void set(String key, Object value) {
        Setting s = settingDao.select(key);
        String val = jsonHelper.object2json(value);
        if (s == null) {
            s = new Setting();
            s.setKey(key);
            s.setValue(val);
            settingDao.insert(s);
        } else {
            s.setValue(val);
            settingDao.update(s);
        }
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz) {

        Setting s = settingDao.select(key);
        return s == null ? null : jsonHelper.json2object(s.getValue(), clazz);  //
    }

    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        Setting s = settingDao.select(key);
        return s == null ? null : jsonHelper.json2List(s.getValue(), clazz);
    }

    @Resource
    private JsonHelper jsonHelper;
    @Resource
    private SettingDao settingDao;
    private Logger logger = LoggerFactory.getLogger(SiteServiceImpl.class);

    public void setSettingDao(SettingDao settingDao) {
        this.settingDao = settingDao;
    }

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }
}
