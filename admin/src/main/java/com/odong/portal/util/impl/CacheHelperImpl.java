package com.odong.portal.util.impl;

import com.odong.portal.util.CacheHelper;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-23
 * Time: 上午12:28
 */
@Component
public class CacheHelperImpl implements CacheHelper {
    @Override
    public void delete(String key) {
        try {
            client.delete(key);
        } catch (MemcachedException | TimeoutException | InterruptedException e) {
            logger.error("删除缓存[{}]出错", key, e);

        }

    }

    @Override
    public void touch(String key, int timeout) {
        try {
            client.touch(key, timeout);
        } catch (MemcachedException | TimeoutException | InterruptedException e) {
            logger.error("延长缓存有效期[{}]出错", key, e);

        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            return client.get(key);
        } catch (MemcachedException | TimeoutException | InterruptedException e) {
            logger.error("取得缓存[{}]出错", key, e);
        }
        return null;
    }

    @Override
    public void set(String key, int timeout, Object object) {
        try {
            client.set(key, timeout, object);
        } catch (MemcachedException | TimeoutException | InterruptedException e) {
            logger.error("取得缓存[{}]出错", key, e);

        }
    }

    @Resource
    private MemcachedClient client;
    private final static Logger logger = LoggerFactory.getLogger(CacheHelperImpl.class);

    public void setClient(MemcachedClient client) {
        this.client = client;
    }

}
