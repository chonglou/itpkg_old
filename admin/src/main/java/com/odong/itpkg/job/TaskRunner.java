package com.odong.itpkg.job;

import com.odong.itpkg.entity.Task;
import com.odong.itpkg.rpc.RpcHelper;
import com.odong.itpkg.service.TaskService;
import com.odong.itpkg.util.DBHelper;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.itpkg.util.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午10:42
 */
public class TaskRunner implements Runnable {
    @Override
    public void run() {

        Task task = taskService.get(taskId);
        if (task.getState() != Task.State.SUBMIT) {
            throw new IllegalArgumentException("任务[" + taskId + "]状态为：" + task.getState());
        }

        Date now = new Date();
        if (now.compareTo(task.getStartUp()) < 0) {
            throw new IllegalArgumentException("任务[" + taskId + "]起始时间为：" + task.getStartUp());
        }
        if (now.compareTo(task.getShutDown()) > 0) {
            if (task.getState() != Task.State.DONE) {
                taskService.setState(taskId, Task.State.DONE);
            }
            throw new IllegalArgumentException("过期任务[" + taskId + "]");
        }

        taskService.setBegin(taskId);
        try {

            long hostId;
            switch (task.getType()) {
                case RPC_COMMAND:
                    List<String> commands = jsonHelper.json2List(task.getRequest(), String.class);
                    hostId = Long.parseLong(commands.get(0));
                    rpcHelper.command(hostId, commands.subList(1, commands.size()).toArray(new String[1]));
                    break;
                case RPC_FILE:
                    List<String> lines = jsonHelper.json2List(task.getRequest(), String.class);
                    hostId = Long.parseLong(lines.get(0));
                    rpcHelper.file(hostId, lines.get(1), lines.get(2), lines.get(3), lines.subList(4, lines.size()).toArray(new String[1]));
                    break;
                case RPC_HEART:
                    List<String> heart = jsonHelper.json2List(task.getRequest(), String.class);
                    hostId = Long.parseLong(heart.get(0));
                    rpcHelper.heart(hostId);
                    break;
                case DB_BACKUP:
                    dbHelper.backup();
                    taskService.setEnd(taskId, null);
                    break;
                case SYS_GC:
                    System.gc();
                    taskService.setEnd(taskId, null);
                    break;
            }
        } catch (Exception e) {
            logger.error("执行任务[{}]失败", task.getId(), e);
            taskService.setEnd(taskId, e.getMessage());
        }


    }


    public TaskRunner(String taskId,
                      TaskService taskService,
                      JsonHelper jsonHelper,
                      EncryptHelper encryptHelper,
                      RpcHelper rpcHelper,
                      DBHelper dbHelper) {
        this.taskId = taskId;
        this.jsonHelper = jsonHelper;
        this.encryptHelper = encryptHelper;
        this.taskService = taskService;
        this.dbHelper = dbHelper;
        this.rpcHelper = rpcHelper;
    }

    private RpcHelper rpcHelper;
    private String taskId;
    private JsonHelper jsonHelper;
    private EncryptHelper encryptHelper;
    private TaskService taskService;
    private DBHelper dbHelper;
    private final static Logger logger = LoggerFactory.getLogger(TaskRunner.class);
}
