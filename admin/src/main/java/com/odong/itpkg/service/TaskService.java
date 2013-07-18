package com.odong.itpkg.service;

import com.odong.itpkg.entity.Task;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 上午9:48
 */
public interface TaskService {
    void setState(String id, Task.State state);

    void setBegin(String id);

    void setEnd(String id, String response);

    Task get(String id);

    String add(Task.Type type, String request);

    String add(Task.Type type, String request, Date startUp, Date shutDown, int total, int space);

    List<Task> listTimerTask();
}
