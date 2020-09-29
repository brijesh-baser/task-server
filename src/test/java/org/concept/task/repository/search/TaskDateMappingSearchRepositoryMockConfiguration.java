package org.concept.task.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link TaskDateMappingSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class TaskDateMappingSearchRepositoryMockConfiguration {

    @MockBean
    private TaskDateMappingSearchRepository mockTaskDateMappingSearchRepository;

}
