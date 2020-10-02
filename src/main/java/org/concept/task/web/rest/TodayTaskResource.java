package org.concept.task.web.rest;

import io.github.jhipster.web.util.PaginationUtil;
import org.concept.task.domain.Task;
import org.concept.task.service.TodayTaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TodayTaskResource {

    private final TodayTaskService todayTaskService;

    public TodayTaskResource(TodayTaskService todayTaskService) {
        this.todayTaskService = todayTaskService;
    }

    @GetMapping("/_today/tasks")
    public ResponseEntity<List<Task>> getTodayTasks(@RequestParam String query, Pageable pageable) {
        Page<Task> todayTasks = todayTaskService.getTodayTasks(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), todayTasks);
        return ResponseEntity.ok().headers(headers).body(todayTasks.getContent());
    }

    @PostMapping("/add-today-task/{taskId}")
    public ResponseEntity<Void> addTodayTask(@PathVariable Long taskId) {
        todayTaskService.addToTodayTask(taskId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove-today-task/{taskId}")
    public ResponseEntity<Void> removeTodayTask(@PathVariable Long taskId) {
        todayTaskService.removeFromTodayTask(taskId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/is-today-task/{taskId}")
    public ResponseEntity<Map<String, Boolean>> isTodayTask(@PathVariable Long taskId) {
        Boolean isTodayTask = todayTaskService.isTodayTask(taskId);
        Map<String, Boolean> todayTaskMap = new HashMap<>();
        todayTaskMap.put("is-today-task", isTodayTask);
        return ResponseEntity.ok(todayTaskMap);
    }
}
