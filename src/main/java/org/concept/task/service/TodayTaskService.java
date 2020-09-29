package org.concept.task.service;

import org.concept.task.domain.Task;

import java.util.List;

public interface TodayTaskService {

    List<Task> getTodayTasks();

    void addToTodayTask(Long taskId);

    void removeFromTodayTask(Long taskId);

    boolean isTodayTask(Long taskId);
}
