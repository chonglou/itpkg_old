package com.odong.portal.util;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-23
 * Time: 上午12:28
 */
public interface CacheHelper {
    void delete(String key);

    void touch(String key, int timeout);

    <T> T get(String key, Class<T> clazz);

    void set(String key, int timeout, Object object);
}
