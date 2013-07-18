package com.odong.itpkg.dao.impl;

import com.odong.itpkg.dao.TaskDao;
import com.odong.itpkg.entity.Task;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 下午12:32
 */
@Repository("site.taskDao")
public class TaskDaoImpl extends BaseJpa2DaoImpl<Task, String> implements TaskDao {
}
