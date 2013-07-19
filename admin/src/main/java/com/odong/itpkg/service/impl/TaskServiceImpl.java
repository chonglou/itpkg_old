package com.odong.itpkg.service.impl;

import com.odong.itpkg.dao.TaskDao;
import com.odong.itpkg.entity.Task;
import com.odong.itpkg.service.TaskService;
import com.odong.itpkg.util.JsonHelper;
import com.odong.portal.util.TimeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 上午9:48
 */
@Service("taskService")
public class TaskServiceImpl implements TaskService {

    @Override
    public String addFile(long host, String name, String mode, List<String> commands) {
        commands.add(0, Long.toString(host));
        commands.add(1, name);
        commands.add(2, mode);
        return add(Task.Type.RPC_FILE, jsonHelper.object2json(commands));
    }

    @Override
    public String addCommand(long host, List<String> commands) {
        commands.add(0, Long.toString(host));
        return add(Task.Type.RPC_COMMAND, jsonHelper.object2json(commands));
    }

    @Override
    public String addHeart(long host, int space) {
        if (space < 3) {
            throw new IllegalArgumentException("心跳时间过短：" + space);
        }
        return add(Task.Type.RPC_HEART, Long.toString(host), new Date(), timeHelper.max(), 0, space);
    }

    @Override
    public void setState(String id, Task.State state) {
        Task t = taskDao.select(id);
        t.setState(state);
        taskDao.update(t);
    }

    @Override
    public void setBegin(String id) {
        Task t = taskDao.select(id);
        t.setLastBegin(new Date());
        t.setState(Task.State.PROCESS);
        taskDao.update(t);
    }

    @Override
    public void setEnd(String id, String response) {
        Task t = taskDao.select(id);
        t.setResponse(response);
        Date now = new Date();
        t.setLastEnd(now);
        t.setIndex(t.getIndex() + 1);
        t.setState(t.getTotal() == null ||
                (t.getTotal() > 0 && t.getTotal() == t.getIndex()) ||
                now.compareTo(t.getShutDown()) >= 0
                ? Task.State.DONE : Task.State.SUBMIT);

        taskDao.update(t);
    }

    @Override
    public Task get(String id) {
        return taskDao.select(id);
    }

    @Override
    public String add(Task.Type type, String request) {
        return add(type, request, new Date(), timeHelper.max(), 1, 0);
    }

    @Override
    public String add(Task.Type type, String request, Date startUp, Date shutDown, int total, int space) {
        String id = UUID.randomUUID().toString();
        Task task = new Task();
        task.setId(id);
        task.setType(type);
        task.setRequest(request);
        task.setStartUp(startUp);
        task.setShutDown(shutDown);
        task.setTotal(total);
        task.setSpace(space);
        task.setIndex(0);
        task.setState(Task.State.SUBMIT);
        task.setCreated(new Date());
        taskDao.insert(task);
        return id;
    }

    @Override
    public List<Task> listTimerTask() {
        Map<String, Object> map = new HashMap<>();
        map.put("total", null);
        map.put("state", Task.State.SUBMIT);
        map.put("now", new Date());
        return taskDao.list("FROM Task as i WHERE i.total!=:total AND i.state=:state AND i.startUp>=:now", map);
    }

    @Override
    public void removeInvalid(int daysKeep) {
        Map<String, Object> map = new HashMap<>();
        map.put("date", timeHelper.plus(new Date(), -60 * 60 * 24 * daysKeep));
        map.put("state", Task.State.DONE);
        taskDao.delete("DELETE Task AS i WHERE i.created < :date AND i.state=:state", map);
    }

    @Resource
    private TaskDao taskDao;
    @Resource
    private TimeHelper timeHelper;
    @Resource
    private JsonHelper jsonHelper;
    private final static Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public void setTimeHelper(TimeHelper timeHelper) {
        this.timeHelper = timeHelper;
    }

    public void setTaskDao(TaskDao taskDao) {
        this.taskDao = taskDao;
    }
}
