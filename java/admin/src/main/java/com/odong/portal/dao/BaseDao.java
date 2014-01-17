package com.odong.portal.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-22
 * Time: 下午2:30
 */
public interface BaseDao<T extends Serializable, PK extends Serializable> {
    T select(PK id);

    T select(String hql, Map<String, Object> map);

    void insert(T t);

    void delete(PK id);

    void delete(String hql, Map<String, Object> map);

    List<T> list();

    List<T> list(String hql, Map<String, Object> map, int count);

    List<T> list(String hql, Map<String, Object> map);

    List<T> list(int pageNo, int pageSize, String hql, Map<String, Object> map);

    long count();

    long count(String hql, Map<String, Object> map);

    void update(T t);

    void update(String hql, Map<String, Object> map);

    String hqlListAll();
}
