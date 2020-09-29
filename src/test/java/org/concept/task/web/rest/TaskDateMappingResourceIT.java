package org.concept.task.web.rest;

import org.concept.task.TaskApp;
import org.concept.task.domain.TaskDateMapping;
import org.concept.task.domain.Task;
import org.concept.task.repository.TaskDateMappingRepository;
import org.concept.task.repository.search.TaskDateMappingSearchRepository;
import org.concept.task.service.TaskDateMappingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link TaskDateMappingResource} REST controller.
 */
@SpringBootTest(classes = TaskApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class TaskDateMappingResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private TaskDateMappingRepository taskDateMappingRepository;

    @Autowired
    private TaskDateMappingService taskDateMappingService;

    /**
     * This repository is mocked in the org.concept.task.repository.search test package.
     *
     * @see org.concept.task.repository.search.TaskDateMappingSearchRepositoryMockConfiguration
     */
    @Autowired
    private TaskDateMappingSearchRepository mockTaskDateMappingSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskDateMappingMockMvc;

    private TaskDateMapping taskDateMapping;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskDateMapping createEntity(EntityManager em) {
        TaskDateMapping taskDateMapping = new TaskDateMapping()
            .date(DEFAULT_DATE);
        // Add required entity
        Task task;
        if (TestUtil.findAll(em, Task.class).isEmpty()) {
            task = TaskResourceIT.createEntity(em);
            em.persist(task);
            em.flush();
        } else {
            task = TestUtil.findAll(em, Task.class).get(0);
        }
        taskDateMapping.setTask(task);
        return taskDateMapping;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskDateMapping createUpdatedEntity(EntityManager em) {
        TaskDateMapping taskDateMapping = new TaskDateMapping()
            .date(UPDATED_DATE);
        // Add required entity
        Task task;
        if (TestUtil.findAll(em, Task.class).isEmpty()) {
            task = TaskResourceIT.createUpdatedEntity(em);
            em.persist(task);
            em.flush();
        } else {
            task = TestUtil.findAll(em, Task.class).get(0);
        }
        taskDateMapping.setTask(task);
        return taskDateMapping;
    }

    @BeforeEach
    public void initTest() {
        taskDateMapping = createEntity(em);
    }

    @Test
    @Transactional
    public void createTaskDateMapping() throws Exception {
        int databaseSizeBeforeCreate = taskDateMappingRepository.findAll().size();
        // Create the TaskDateMapping
        restTaskDateMappingMockMvc.perform(post("/api/task-date-mappings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(taskDateMapping)))
            .andExpect(status().isCreated());

        // Validate the TaskDateMapping in the database
        List<TaskDateMapping> taskDateMappingList = taskDateMappingRepository.findAll();
        assertThat(taskDateMappingList).hasSize(databaseSizeBeforeCreate + 1);
        TaskDateMapping testTaskDateMapping = taskDateMappingList.get(taskDateMappingList.size() - 1);
        assertThat(testTaskDateMapping.getDate()).isEqualTo(DEFAULT_DATE);

        // Validate the TaskDateMapping in Elasticsearch
        verify(mockTaskDateMappingSearchRepository, times(1)).save(testTaskDateMapping);
    }

    @Test
    @Transactional
    public void createTaskDateMappingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = taskDateMappingRepository.findAll().size();

        // Create the TaskDateMapping with an existing ID
        taskDateMapping.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskDateMappingMockMvc.perform(post("/api/task-date-mappings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(taskDateMapping)))
            .andExpect(status().isBadRequest());

        // Validate the TaskDateMapping in the database
        List<TaskDateMapping> taskDateMappingList = taskDateMappingRepository.findAll();
        assertThat(taskDateMappingList).hasSize(databaseSizeBeforeCreate);

        // Validate the TaskDateMapping in Elasticsearch
        verify(mockTaskDateMappingSearchRepository, times(0)).save(taskDateMapping);
    }


    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskDateMappingRepository.findAll().size();
        // set the field null
        taskDateMapping.setDate(null);

        // Create the TaskDateMapping, which fails.


        restTaskDateMappingMockMvc.perform(post("/api/task-date-mappings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(taskDateMapping)))
            .andExpect(status().isBadRequest());

        List<TaskDateMapping> taskDateMappingList = taskDateMappingRepository.findAll();
        assertThat(taskDateMappingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTaskDateMappings() throws Exception {
        // Initialize the database
        taskDateMappingRepository.saveAndFlush(taskDateMapping);

        // Get all the taskDateMappingList
        restTaskDateMappingMockMvc.perform(get("/api/task-date-mappings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskDateMapping.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getTaskDateMapping() throws Exception {
        // Initialize the database
        taskDateMappingRepository.saveAndFlush(taskDateMapping);

        // Get the taskDateMapping
        restTaskDateMappingMockMvc.perform(get("/api/task-date-mappings/{id}", taskDateMapping.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(taskDateMapping.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingTaskDateMapping() throws Exception {
        // Get the taskDateMapping
        restTaskDateMappingMockMvc.perform(get("/api/task-date-mappings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTaskDateMapping() throws Exception {
        // Initialize the database
        taskDateMappingService.save(taskDateMapping);

        int databaseSizeBeforeUpdate = taskDateMappingRepository.findAll().size();

        // Update the taskDateMapping
        TaskDateMapping updatedTaskDateMapping = taskDateMappingRepository.findById(taskDateMapping.getId()).get();
        // Disconnect from session so that the updates on updatedTaskDateMapping are not directly saved in db
        em.detach(updatedTaskDateMapping);
        updatedTaskDateMapping
            .date(UPDATED_DATE);

        restTaskDateMappingMockMvc.perform(put("/api/task-date-mappings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedTaskDateMapping)))
            .andExpect(status().isOk());

        // Validate the TaskDateMapping in the database
        List<TaskDateMapping> taskDateMappingList = taskDateMappingRepository.findAll();
        assertThat(taskDateMappingList).hasSize(databaseSizeBeforeUpdate);
        TaskDateMapping testTaskDateMapping = taskDateMappingList.get(taskDateMappingList.size() - 1);
        assertThat(testTaskDateMapping.getDate()).isEqualTo(UPDATED_DATE);

        // Validate the TaskDateMapping in Elasticsearch
        verify(mockTaskDateMappingSearchRepository, times(2)).save(testTaskDateMapping);
    }

    @Test
    @Transactional
    public void updateNonExistingTaskDateMapping() throws Exception {
        int databaseSizeBeforeUpdate = taskDateMappingRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskDateMappingMockMvc.perform(put("/api/task-date-mappings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(taskDateMapping)))
            .andExpect(status().isBadRequest());

        // Validate the TaskDateMapping in the database
        List<TaskDateMapping> taskDateMappingList = taskDateMappingRepository.findAll();
        assertThat(taskDateMappingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TaskDateMapping in Elasticsearch
        verify(mockTaskDateMappingSearchRepository, times(0)).save(taskDateMapping);
    }

    @Test
    @Transactional
    public void deleteTaskDateMapping() throws Exception {
        // Initialize the database
        taskDateMappingService.save(taskDateMapping);

        int databaseSizeBeforeDelete = taskDateMappingRepository.findAll().size();

        // Delete the taskDateMapping
        restTaskDateMappingMockMvc.perform(delete("/api/task-date-mappings/{id}", taskDateMapping.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TaskDateMapping> taskDateMappingList = taskDateMappingRepository.findAll();
        assertThat(taskDateMappingList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the TaskDateMapping in Elasticsearch
        verify(mockTaskDateMappingSearchRepository, times(1)).deleteById(taskDateMapping.getId());
    }

    @Test
    @Transactional
    public void searchTaskDateMapping() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        taskDateMappingService.save(taskDateMapping);
        when(mockTaskDateMappingSearchRepository.search(queryStringQuery("id:" + taskDateMapping.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(taskDateMapping), PageRequest.of(0, 1), 1));

        // Search the taskDateMapping
        restTaskDateMappingMockMvc.perform(get("/api/_search/task-date-mappings?query=id:" + taskDateMapping.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskDateMapping.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }
}
