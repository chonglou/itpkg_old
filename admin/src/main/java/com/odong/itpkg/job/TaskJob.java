package com.odong.itpkg.job;

import com.odong.itpkg.entity.Task;
import com.odong.itpkg.service.TaskService;
import com.odong.itpkg.util.TaskHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-6-4
 * Time: 下午12:46
 */
@Component("job.taskTarget")
public class TaskJob {
    public void execute() {
        for (Task t : taskService.listTimerTask()) {
            taskService.setBegin(t.getId());
            String response = taskHelper.execute(t.getId());
            taskService.setEnd(t.getId(), response);
        }
    }

    @Resource
    private TaskService taskService;
    @Resource
    private TaskHelper taskHelper;

    public void setTaskHelper(TaskHelper taskHelper) {
        this.taskHelper = taskHelper;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }
}
