package org.concept.task.listener.impl;

import org.concept.task.domain.Task;
import org.concept.task.listener.TaskListener;
import org.concept.task.service.TaskDateMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class TaskListenerImpl implements TaskListener {

    private final Logger log = LoggerFactory.getLogger(TaskListenerImpl.class);

    private final TaskDateMappingService taskDateMappingService;

    public TaskListenerImpl(TaskDateMappingService taskDateMappingService) {
        this.taskDateMappingService = taskDateMappingService;
    }

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTaskChange(Task task) {
        log.debug("onTaskChange() task := " + task);
        taskDateMappingService.reIndex(task.getId());
    }
}
