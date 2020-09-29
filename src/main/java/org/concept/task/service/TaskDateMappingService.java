package org.concept.task.service;

import org.concept.task.domain.Task;
import org.concept.task.domain.TaskDateMapping;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link TaskDateMapping}.
 */
public interface TaskDateMappingService {

    /**
     * Save a taskDateMapping.
     *
     * @param taskDateMapping the entity to save.
     * @return the persisted entity.
     */
    TaskDateMapping save(TaskDateMapping taskDateMapping);

    /**
     * Get all the taskDateMappings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TaskDateMapping> findAll(Pageable pageable);


    /**
     * Get the "id" taskDateMapping.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TaskDateMapping> findOne(Long id);

    /**
     * Delete the "id" taskDateMapping.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the taskDateMapping corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TaskDateMapping> search(String query, Pageable pageable);

    void reIndex();

    void reIndex(Long taskId);
}
