package com.odong.itpkg.util.impl;

import com.odong.itpkg.entity.Task;
import com.odong.itpkg.service.TaskService;
import com.odong.itpkg.util.DBHelper;
import com.odong.itpkg.util.JsonHelper;
import com.odong.itpkg.util.TaskHelper;
import com.odong.portal.util.TimeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 下午7:42
 */
@Component
public class TaskHelperImpl implements TaskHelper {
    @Override
    public String addFile(long host, String name, String mode, List<String> commands) {
        commands.add(0, Long.toString(host));
        commands.add(1, name);
        commands.add(2, mode);
        return taskService.add(Task.Type.RPC_FILE, jsonHelper.object2json(commands));
    }

    @Override
    public String addCommand(long host, List<String> commands) {
        commands.add(0, Long.toString(host));
        return taskService.add(Task.Type.RPC_COMMAND, jsonHelper.object2json(commands));
    }

    @Override
    public String addHeart(long host, int space) {
        assert space > 3;
        return taskService.add(Task.Type.RPC_HEART, Long.toString(host), new Date(), timeHelper.max(), 0, space);
    }

    @Override
    public String execute(String id) {
        Task task = taskService.get(id);
        assert task.getState() != Task.State.DONE;
        Date now = new Date();
        assert now.compareTo(task.getStartUp()) > 0;
        if (now.compareTo(task.getShutDown()) > 0) {
            if (task.getState() != Task.State.DONE) {
                taskService.setState(id, Task.State.DONE);
            }
            logger.error("任务[{}]已经过期", id);
            throw new IllegalArgumentException("过期任务[" + id + "]");
        }
        String msg = null;
        try {
            //TODO 需要完善
            switch (task.getType()) {
                case RPC_COMMAND:
                    break;
                case RPC_FILE:
                    break;
                case RPC_HEART:
                    break;
                case DB_BACKUP:
                    dbHelper.backup();
                    break;
                case SYS_GC:
                    System.gc();
                    break;
            }
        } catch (Exception e) {
            logger.error("执行任务[{}]失败", task.getId(), e);
            msg = e.getMessage();
        }
        return msg;
    }

    @Resource
    private TaskService taskService;
    @Resource
    private JsonHelper jsonHelper;
    @Resource
    private DBHelper dbHelper;
    @Resource
    private TimeHelper timeHelper;
    private final static Logger logger = LoggerFactory.getLogger(TaskHelperImpl.class);

    public void setTimeHelper(TimeHelper timeHelper) {
        this.timeHelper = timeHelper;
    }

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public void setDbHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

}
