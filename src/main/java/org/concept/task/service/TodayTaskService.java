package org.concept.task.service;

import org.concept.task.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TodayTaskService {

    Page<Task> getTodayTasks(String query, Pageable pageable);

    void addToTodayTask(Long taskId);

    void removeFromTodayTask(Long taskId);

    boolean isTodayTask(Long taskId);
}
