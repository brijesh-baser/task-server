package org.concept.task.service.impl;

import org.concept.task.domain.enumeration.TaskStatus;
import org.concept.task.security.SecurityUtils;
import org.concept.task.service.TaskService;
import org.concept.task.domain.Task;
import org.concept.task.repository.TaskRepository;
import org.concept.task.repository.search.TaskSearchRepository;
import org.concept.task.web.rest.errors.BadRequestAlertException;
import org.elasticsearch.index.query.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Task}.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;

    private final TaskSearchRepository taskSearchRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    public TaskServiceImpl(TaskRepository taskRepository,
                           TaskSearchRepository taskSearchRepository,
                           ApplicationEventPublisher applicationEventPublisher) {
        this.taskRepository = taskRepository;
        this.taskSearchRepository = taskSearchRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Task save(Task task) {
        log.debug("Request to save Task : {}", task);
        Task result = taskRepository.save(task);
        taskSearchRepository.save(result);
        applicationEventPublisher.publishEvent(task);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Task> findAll(Pageable pageable) {
        log.debug("Request to get all Tasks");
        return taskRepository.findAll(pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Task> findOne(Long id) {
        log.debug("Request to get Task : {}", id);
        return taskRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Task : {}", id);
        taskRepository.deleteById(id);
        taskSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Task> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Tasks for query {}", query);
        return taskSearchRepository.search(queryStringQuery(query).defaultOperator(Operator.AND), pageable);
    }

    @Override
    public Page<Task> getAllTask(String query, Pageable pageable) {
        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        if(login == null || login.isEmpty()) {
            throw new BadRequestAlertException("Please login before using api", "Task", "login.failed");
        }
        return search("user.login:" + login + " AND " + query, pageable);
    }

    @Override
    public Page<Task> getPendingTask(String query, Pageable pageable) {
        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        if(login == null || login.isEmpty()) {
            throw new BadRequestAlertException("Please login before using api", "Task", "login.failed");
        }
        return search("user.login:" + login + " AND task.status:" + TaskStatus.IN_PROGRESS + " AND " + query, pageable);
    }

    @Override
    public Page<Task> getCompletedTask(String query, Pageable pageable) {
        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        if(login == null || login.isEmpty()) {
            throw new BadRequestAlertException("Please login before using api", "Task", "login.failed");
        }
        return search("user.login:" + login + " AND task.status:" + TaskStatus.DONE + " AND " + query, pageable);
    }
}
