package com.odong.itpkg.job;

import com.odong.itpkg.entity.Task;
import com.odong.itpkg.service.TaskService;
import com.odong.itpkg.util.DBHelper;
import com.odong.itpkg.util.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午10:42
 */
public class TaskRunner implements Runnable {
    @Override
    public void run() {

        Task task = taskService.get(id);
        if (task.getState() != Task.State.SUBMIT) {
            throw new IllegalArgumentException("任务[" + id + "]状态为：" + task.getState());
        }

        Date now = new Date();
        if (now.compareTo(task.getStartUp()) < 0) {
            throw new IllegalArgumentException("任务[" + id + "]起始时间为：" + task.getStartUp());
        }
        if (now.compareTo(task.getShutDown()) > 0) {
            if (task.getState() != Task.State.DONE) {
                taskService.setState(id, Task.State.DONE);
            }
            throw new IllegalArgumentException("过期任务[" + id + "]");
        }

        taskService.setBegin(id);
        String response = null;
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
            response = e.getMessage();
        }

        taskService.setEnd(id, response);
    }

    public TaskRunner(String id, JsonHelper jsonHelper, TaskService taskService, DBHelper dbHelper) {
        this.id = id;
        this.jsonHelper = jsonHelper;
        this.taskService = taskService;
        this.dbHelper = dbHelper;
    }

    private String id;

    private JsonHelper jsonHelper;
    private TaskService taskService;
    private DBHelper dbHelper;
    private final static Logger logger = LoggerFactory.getLogger(TaskRunner.class);
}
