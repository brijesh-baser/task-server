package org.concept.task.service.impl;

import org.concept.task.domain.Task;
import org.concept.task.service.TaskDateMappingService;
import org.concept.task.domain.TaskDateMapping;
import org.concept.task.repository.TaskDateMappingRepository;
import org.concept.task.repository.search.TaskDateMappingSearchRepository;
import org.concept.task.web.rest.errors.BadRequestAlertException;
import org.elasticsearch.index.query.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link TaskDateMapping}.
 */
@Service
@Transactional
public class TaskDateMappingServiceImpl implements TaskDateMappingService {

    private final Logger log = LoggerFactory.getLogger(TaskDateMappingServiceImpl.class);

    private final TaskDateMappingRepository taskDateMappingRepository;

    private final TaskDateMappingSearchRepository taskDateMappingSearchRepository;

    public TaskDateMappingServiceImpl(TaskDateMappingRepository taskDateMappingRepository, TaskDateMappingSearchRepository taskDateMappingSearchRepository) {
        this.taskDateMappingRepository = taskDateMappingRepository;
        this.taskDateMappingSearchRepository = taskDateMappingSearchRepository;
    }

    @Override
    public TaskDateMapping save(TaskDateMapping taskDateMapping) {
        log.debug("Request to save TaskDateMapping : {}", taskDateMapping);
        TaskDateMapping result = taskDateMappingRepository.save(taskDateMapping);
        taskDateMappingSearchRepository.save(result);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDateMapping> findAll(Pageable pageable) {
        log.debug("Request to get all TaskDateMappings");
        return taskDateMappingRepository.findAll(pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<TaskDateMapping> findOne(Long id) {
        log.debug("Request to get TaskDateMapping : {}", id);
        return taskDateMappingRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TaskDateMapping : {}", id);
        taskDateMappingRepository.deleteById(id);
        taskDateMappingSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDateMapping> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TaskDateMappings for query {}", query);
        return taskDateMappingSearchRepository.search(queryStringQuery(query).defaultOperator(Operator.AND), pageable);
    }

    @Override
    public void reIndex() {
        taskDateMappingSearchRepository.saveAll(taskDateMappingRepository.findAll());
    }

    @Override
    public void reIndex(Long taskId) {
        List<TaskDateMapping> taskDateMappingList = taskDateMappingRepository.getTaskDateMappingByTaskId(taskId);
        if(!taskDateMappingList.isEmpty()) {
            taskDateMappingSearchRepository.saveAll(taskDateMappingList);
        }
    }
}
