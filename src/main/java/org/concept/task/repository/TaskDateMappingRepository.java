package org.concept.task.repository;

import org.concept.task.domain.TaskDateMapping;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the TaskDateMapping entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskDateMappingRepository extends JpaRepository<TaskDateMapping, Long> {

    @Query(value = "select * from task_date_mapping where task_id=:taskId", nativeQuery = true)
    List<TaskDateMapping> getTaskDateMappingByTaskId(@Param("taskId") Long taskId);
}
