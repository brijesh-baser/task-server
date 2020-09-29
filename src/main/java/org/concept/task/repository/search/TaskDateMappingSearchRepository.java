package org.concept.task.repository.search;

import org.concept.task.domain.TaskDateMapping;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link TaskDateMapping} entity.
 */
public interface TaskDateMappingSearchRepository extends ElasticsearchRepository<TaskDateMapping, Long> {
}
