package com.odong.itpkg.service.impl;

import com.odong.itpkg.dao.TaskDao;
import com.odong.itpkg.entity.Task;
import com.odong.itpkg.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 上午9:48
 */
@Service("taskService")
public class TaskServiceImpl implements TaskService {
    @Override
    public String addRpcCommand(long host, List<String> commands) {
        return null;  //
    }

    @Override
    public String addRpcFile(String name, String mode, List<String> lines) {
        return null;  //
    }

    @Override
    public String addRpcHeart(int space) {

        return null;  //
    }

    @Override
    public void add(Task task) {
        taskDao.insert(task);
    }

    @Override
    public void execute(Task task) {

        if (task.getState() == Task.State.DONE) {
            logger.error("任务[{}]已经结束", task.getId());
            return;
        }
        Date now = new Date();
        if(task.getStartUp()!=null && now.compareTo(task.getStartUp())<0){
            logger.error("任务[{}]还未生效", task.getId());
            return;
        }
        if(task.getShutDown()!=null && now.compareTo(task.getShutDown())>0){
            logger.error("任务[{}]已经过期", task.getId());
            return;
        }
        task.setLastBegin(new Date());
        try {
            switch (task.getType()) {
                case RPC_COMMAND:
                    break;
                case RPC_FILE:
                    break;
                case RPC_HEART:
                    break;
                case MYSQL_BACKUP:
                    break;
                case SYS_GC:
                    System.gc();
                    break;
            }
        } catch (Exception e) {
            logger.error("执行任务[{}]失败", task.getId(), e);
            task.setResponse(e.getMessage());
        }
        task.setLastEnd(new Date());
        task.setIndex(task.getIndex()+1);
        task.setState(task.getIndex() < task.getTotal() ? Task.State.SUBMIT : Task.State.DONE);
        taskDao.update(task);
    }

    @Override
    public List<Task> listTimerTask() {
        Map<String,Object> map = new HashMap<>();
        map.put("now", new Date());
        map.put("state", Task.State.DONE);
        return taskDao.list("FROM Task as i WHERE i.total!=1 AND i.state!=DONE AND i.startUp <:now", map);
    }

    @Resource
    private TaskDao taskDao;
    private final static Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    public void setTaskDao(TaskDao taskDao) {
        this.taskDao = taskDao;
    }
}
