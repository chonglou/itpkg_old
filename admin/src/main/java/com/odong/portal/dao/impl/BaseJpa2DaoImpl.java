package com.odong.portal.dao.impl;

import com.odong.portal.dao.BaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-23
 * Time: 下午9:27
 */
public class BaseJpa2DaoImpl<T extends Serializable, PK extends Serializable> implements BaseDao<T, PK> {
    @Override
    public T select(PK id) {
        return entityManager.find(clazz, id);  //
    }

    @Override
    public T select(String hql, Map<String, Object> map) {
        TypedQuery<T> query = entityManager.createQuery(hql, clazz);
        if (map != null) {
            for (String key : map.keySet()) {
                query.setParameter(key, map.get(key));
            }
        }
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void insert(T t) {
        entityManager.persist(t);
    }

    @Override
    public void delete(PK id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        update("DELETE " + tableName() + " i WHERE i." + pkName + "=:id", map);
    }

    @Override
    public void delete(String hql, Map<String, Object> map) {
        update(hql, map);
    }

    @Override
    public List<T> list() {
        return list(hqlListAll(), null);
    }

    @Override
    public List<T> list(String hql, Map<String, Object> map, int count) {
        TypedQuery<T> query = entityManager.createQuery(hql, clazz);
        if (map != null) {
            for (String key : map.keySet()) {
                query.setParameter(key, map.get(key));
            }
        }
        query.setMaxResults(count);
        return query.getResultList();
    }

    @Override
    public List<T> list(String hql, Map<String, Object> map) {
        TypedQuery<T> query = entityManager.createQuery(hql, clazz);
        if (map != null) {
            for (String key : map.keySet()) {
                query.setParameter(key, map.get(key));
            }
        }
        return query.getResultList();  //
    }

    @Override
    public List<T> list(int pageNo, int pageSize, String hql, Map<String, Object> map) {
        TypedQuery<T> query = entityManager.createQuery(hql, clazz);
        if (map != null) {
            for (String key : map.keySet()) {
                query.setParameter(key, map.get(key));
            }
        }
        query.setFirstResult((pageNo - 1) * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public long count() {
        return count("SELECT COUNT(*) FROM " + tableName(), null);
    }

    @Override
    public long count(String hql, Map<String, Object> map) {
        TypedQuery<Long> query = entityManager.createQuery(hql, Long.class);
        if (map != null) {
            for (String key : map.keySet()) {
                query.setParameter(key, map.get(key));
            }
        }
        return query.getSingleResult();  //
    }

    @Override
    public void update(T t) {
        entityManager.merge(t);
    }

    @Override
    public void update(String hql, Map<String, Object> map) {
        Query query = entityManager.createQuery(hql);
        if (map != null) {
            for (String key : map.keySet()) {
                query.setParameter(key, map.get(key));
            }
        }
        query.executeUpdate();
    }

    @Override
    public String hqlListAll() {
        return "SELECT i FROM " + tableName() + " i ORDER BY i." + pkName + " DESC";
    }

    protected void remove(T t) {
        entityManager.remove(t);
    }

    @SuppressWarnings("unchecked")
    public BaseJpa2DaoImpl() {
        this.clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Field[] fields = this.clazz.getDeclaredFields();
        String pk = null;
        for (Field f : fields) {
            if (f.isAnnotationPresent(Id.class)) {
                pk = f.getName();
            }
        }
        if (pk == null) {
            //throw new IllegalArgumentException("类[" + clazz.getSimpleName() + "]没有定义主键");
            pk = "id";
        }
        this.pkName = pk;
    }


    /*
    public BaseDaoJpa2Impl(Class<T> clazz, String pkName){
        this.clazz = clazz;
        this.pkName = pkName;
    }
    */


    protected String tableName() {
        return clazz.getSimpleName();
    }


    @PersistenceContext
    protected EntityManager entityManager;
    protected final Class<T> clazz;
    protected final String pkName;
    private final static Logger logger = LoggerFactory.getLogger(BaseJpa2DaoImpl.class);

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}

