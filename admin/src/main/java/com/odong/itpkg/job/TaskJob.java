package com.odong.itpkg.job;

import com.odong.itpkg.dao.TaskDao;
import com.odong.itpkg.entity.Task;
import com.odong.itpkg.service.TaskService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-6-4
 * Time: 下午12:46
 */
@Component("job.taskTarget")
public class TaskJob {
    public void execute() {
        for(Task t : taskService.listTimerTask()){
            taskService.execute(t);
        }
    }
    @Resource
    private TaskService taskService;

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }
}
