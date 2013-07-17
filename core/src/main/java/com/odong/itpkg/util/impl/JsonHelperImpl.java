package com.odong.itpkg.util.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odong.itpkg.util.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 下午12:13
 */
public class JsonHelperImpl implements JsonHelper {
    @Override
    public String object2json(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("输出JSON出错", e);
        }
        return "{}";
    }

    @Override
    public <T> T json2object(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            logger.debug("解析JSON出错", e);
        }
        return null;
    }

    @Override
    public <K, V> Map<K, V> json2map(String json, Class<K> kClazz, Class<V> vClazz) {
        try {
            return mapper.readValue(json, new TypeReference<Map<K, V>>() {
            });
        } catch (IOException e) {
            logger.debug("解析JSON出错", e);
        }
        return new HashMap<>();
    }

    @Override
    public <T> List<T> json2List(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, new TypeReference<List<T>>() {
            });
        } catch (IOException e) {
            logger.debug("解析JSON出错", e);
        }
        return new ArrayList<>();
    }

    public void init() {
        mapper = new ObjectMapper();
        //mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private ObjectMapper mapper;
    private final static Logger logger = LoggerFactory.getLogger(JsonHelperImpl.class);
}
