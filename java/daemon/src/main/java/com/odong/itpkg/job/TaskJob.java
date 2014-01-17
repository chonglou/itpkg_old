package com.odong.itpkg.job;

import com.odong.itpkg.util.EncryptHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-20
 * Time: 下午7:22
 */
@Component("job.taskTarget")
public class TaskJob {
    public void execute() {
        taskExecutor.execute(new WanIpMonitor(uri, encryptHelper));
    }

    @Resource
    private EncryptHelper encryptHelper;
    @Resource
    private TaskExecutor taskExecutor;
    @Value("${server.http}")
    private String uri;

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
}
