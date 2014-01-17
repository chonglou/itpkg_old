package com.odong.itpkg.util;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午10:54
 */
public interface JsonHelper {
    String object2json(Object object);

    <T> T json2object(String json, Class<T> clazz);

    <K, V> Map<K, V> json2map(String json, Class<K> kClazz, Class<V> vClazz);

    <T> List<T> json2List(String json, Class<T> clazz);
}
