package org.concept.task.service.impl;

import org.concept.task.domain.Task;
import org.concept.task.domain.TaskDateMapping;
import org.concept.task.security.SecurityUtils;
import org.concept.task.service.TaskDateMappingService;
import org.concept.task.service.TaskService;
import org.concept.task.service.TodayTaskService;
import org.concept.task.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TodayTaskServiceImpl implements TodayTaskService {

    private final Logger log = LoggerFactory.getLogger(TodayTaskServiceImpl.class);

    private final TaskService taskService;

    private final TaskDateMappingService taskDateMappingService;

    public TodayTaskServiceImpl(TaskService taskService, TaskDateMappingService taskDateMappingService) {
        this.taskService = taskService;
        this.taskDateMappingService = taskDateMappingService;
    }

    @Override
    public List<Task> getTodayTasks() {
        LocalDate localDate = LocalDate.now();
        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        String query = "task.user.login:" + login + " AND date:[" + localDate + " TO " + localDate + "]";
        log.debug("getTodayTasks() query := " + query);
        List<TaskDateMapping> taskDateMappingList = taskDateMappingService.search(query, PageRequest.of(0, 10000)).getContent();
        return taskDateMappingList.stream().map(TaskDateMapping::getTask).collect(Collectors.toList());
    }

    @Override
    public void addToTodayTask(Long taskId) {
        Task task = taskService.findOne(taskId).orElse(null);
        if(task == null) {
            throw new BadRequestAlertException("Task Not Found", "TodayTask", "task_not_exists");
        }
        if(isTodayTask(taskId)) {
            throw new BadRequestAlertException("Task already added", "TodayTask", "task_already_added");
        }
        log.debug("addToTodayTask task := " + task + ", and date := " + LocalDate.now());
        TaskDateMapping taskDateMapping = new TaskDateMapping();
        taskDateMapping.setTask(task);
        taskDateMapping.setDate(LocalDate.now());
        taskDateMappingService.save(taskDateMapping);
    }

    @Override
    public void removeFromTodayTask(Long taskId) {
        TaskDateMapping taskDateMapping = getTodayTask(taskId);
        if(taskDateMapping == null) {
            throw new BadRequestAlertException("No Task found for today", "TodayTask", "task_not_added");
        }
        log.debug("removeFromTodayTask task := " + taskDateMapping.getTask() + ", and date := " + taskDateMapping.getDate());
        taskDateMappingService.delete(taskDateMapping.getId());
    }

    @Override
    public boolean isTodayTask(Long taskId) {
        TaskDateMapping taskDateMapping = getTodayTask(taskId);
        return taskDateMapping != null;
    }

    private TaskDateMapping getTodayTask(long taskId) {
        LocalDate localDate = LocalDate.now();
        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        String query = "task.user.login:" + login + " AND task.id:" + taskId
            + " AND date:[" + localDate + " TO " + localDate + "]";
        log.debug("getTodayTask() query := " + query);
        List<TaskDateMapping> taskDateMappingList = taskDateMappingService.search(query,
            PageRequest.of(0, 100)).getContent();
        return taskDateMappingList.isEmpty() ? null : taskDateMappingList.get(0);
    }
}
