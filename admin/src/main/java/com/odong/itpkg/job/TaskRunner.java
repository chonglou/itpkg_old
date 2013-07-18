package com.odong.itpkg.job;

import com.odong.itpkg.entity.Task;
import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Ip;
import com.odong.itpkg.model.Rpc;
import com.odong.itpkg.rpc.Callback;
import com.odong.itpkg.rpc.Client;
import com.odong.itpkg.service.NetworkService;
import com.odong.itpkg.service.TaskService;
import com.odong.itpkg.util.DBHelper;
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
        Client client;
        try {

            switch (task.getType()) {
                case RPC_COMMAND:
                    List<String> commands = jsonHelper.json2List(task.getRequest(), String.class);
                    client = createClient(Long.parseLong(commands.get(0)), taskId);
                    client.send(client.command(commands.subList(1, commands.size())));
                    client.send(client.bye());
                    break;
                case RPC_FILE:
                    List<String> lines = jsonHelper.json2List(task.getRequest(), String.class);
                    client = createClient(Long.parseLong(lines.get(0)), taskId);
                    client.send(client.file(lines.get(1), lines.get(2), lines.subList(3, lines.size())));
                    client.send(client.bye());
                    break;
                case RPC_HEART:
                    List<String> heart = jsonHelper.json2List(task.getRequest(), String.class);
                    client = createClient(Long.parseLong(heart.get(0)), taskId);
                    client.send(client.heart());
                    client.send(client.bye());
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

    private Client createClient(long hostId, final String taskId) {
        Host host = networkService.getHost(hostId);
        Ip wan = networkService.getIp(host.getWanIp());
        return new Client(wan.getAddress(), host.getRpcPort(), host.getSignKey(), host.getSignLen(), new Callback() {
            @Override
            public void execute(Rpc.Response response) {
                taskService.setEnd(taskId, jsonHelper.object2json(response.getLinesList()));
            }
        });
    }

    public TaskRunner(String taskId, JsonHelper jsonHelper, TaskService taskService, NetworkService networkService, DBHelper dbHelper) {
        this.taskId = taskId;
        this.jsonHelper = jsonHelper;
        this.taskService = taskService;
        this.networkService = networkService;
        this.dbHelper = dbHelper;
    }

    private String taskId;

    private JsonHelper jsonHelper;
    private TaskService taskService;
    private NetworkService networkService;
    private DBHelper dbHelper;
    private final static Logger logger = LoggerFactory.getLogger(TaskRunner.class);
}
