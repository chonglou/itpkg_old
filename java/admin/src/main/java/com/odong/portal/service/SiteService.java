package com.odong.portal.service;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-22
 * Time: 下午2:28
 */
public interface SiteService {
    Boolean getBoolean(String key);

    Date getDate(String key);

    Long getLong(String key);

    Integer getInteger(String key);

    String getString(String key);

    void set(String key, Object value);

    <T> T getObject(String key, Class<T> clazz);

    <T> List<T> getList(String key, Class<T> clazz);
}
