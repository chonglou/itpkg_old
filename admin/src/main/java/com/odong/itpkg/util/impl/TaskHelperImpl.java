package com.odong.itpkg.util.impl;

import com.odong.itpkg.entity.Task;
import com.odong.itpkg.service.TaskService;
import com.odong.itpkg.util.TaskHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 下午7:42
 */
@Component
public class TaskHelperImpl implements TaskHelper {
    @Override
    public String execute(String id) {
        Task task = taskService.get(id);
        if (task.getState() == Task.State.DONE) {
            logger.error("任务[{}]已经结束", id);
            throw new IllegalArgumentException("过期任务[" + id + "]");
        }
        Date now = new Date();
        if (now.compareTo(task.getStartUp()) < 0) {
            logger.error("任务[{}]还未生效", id);
            throw new IllegalArgumentException("未生效任务[" + id + "]");
        }
        if (now.compareTo(task.getShutDown()) > 0) {
            if (task.getState() != Task.State.DONE) {
                taskService.setState(id, Task.State.DONE);
            }
            logger.error("任务[{}]已经过期", id);
            throw new IllegalArgumentException("过期任务[" + id + "]");
        }
        String msg = null;
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
            msg = e.getMessage();
        }
        return msg;
    }

    @Resource
    private TaskService taskService;
    private final static Logger logger = LoggerFactory.getLogger(TaskHelperImpl.class);

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

}
