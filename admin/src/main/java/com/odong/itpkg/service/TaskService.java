package com.odong.itpkg.service;

import com.odong.itpkg.entity.Task;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 上午9:48
 */
public interface TaskService {
    String addRpcCommand(long host, List<String> commands);
    String addRpcFile(String name, String mode, List<String> lines);
    String addRpcHeart(int space);
    void add(Task task);
    void execute(Task t);
    List<Task> listTimerTask();
}
