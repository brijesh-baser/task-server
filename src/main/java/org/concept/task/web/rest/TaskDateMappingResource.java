package org.concept.task.web.rest;

import org.concept.task.domain.TaskDateMapping;
import org.concept.task.service.TaskDateMappingService;
import org.concept.task.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link org.concept.task.domain.TaskDateMapping}.
 */
@RestController
@RequestMapping("/api")
public class TaskDateMappingResource {

    private final Logger log = LoggerFactory.getLogger(TaskDateMappingResource.class);

    private static final String ENTITY_NAME = "taskDateMapping";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskDateMappingService taskDateMappingService;

    public TaskDateMappingResource(TaskDateMappingService taskDateMappingService) {
        this.taskDateMappingService = taskDateMappingService;
    }

    /**
     * {@code POST  /task-date-mappings} : Create a new taskDateMapping.
     *
     * @param taskDateMapping the taskDateMapping to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taskDateMapping, or with status {@code 400 (Bad Request)} if the taskDateMapping has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/task-date-mappings")
    public ResponseEntity<TaskDateMapping> createTaskDateMapping(@Valid @RequestBody TaskDateMapping taskDateMapping) throws URISyntaxException {
        log.debug("REST request to save TaskDateMapping : {}", taskDateMapping);
        if (taskDateMapping.getId() != null) {
            throw new BadRequestAlertException("A new taskDateMapping cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TaskDateMapping result = taskDateMappingService.save(taskDateMapping);
        return ResponseEntity.created(new URI("/api/task-date-mappings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /task-date-mappings} : Updates an existing taskDateMapping.
     *
     * @param taskDateMapping the taskDateMapping to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskDateMapping,
     * or with status {@code 400 (Bad Request)} if the taskDateMapping is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taskDateMapping couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/task-date-mappings")
    public ResponseEntity<TaskDateMapping> updateTaskDateMapping(@Valid @RequestBody TaskDateMapping taskDateMapping) throws URISyntaxException {
        log.debug("REST request to update TaskDateMapping : {}", taskDateMapping);
        if (taskDateMapping.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TaskDateMapping result = taskDateMappingService.save(taskDateMapping);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, taskDateMapping.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /task-date-mappings} : get all the taskDateMappings.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taskDateMappings in body.
     */
    @GetMapping("/task-date-mappings")
    public ResponseEntity<List<TaskDateMapping>> getAllTaskDateMappings(Pageable pageable) {
        log.debug("REST request to get a page of TaskDateMappings");
        Page<TaskDateMapping> page = taskDateMappingService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /task-date-mappings/:id} : get the "id" taskDateMapping.
     *
     * @param id the id of the taskDateMapping to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskDateMapping, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/task-date-mappings/{id}")
    public ResponseEntity<TaskDateMapping> getTaskDateMapping(@PathVariable Long id) {
        log.debug("REST request to get TaskDateMapping : {}", id);
        Optional<TaskDateMapping> taskDateMapping = taskDateMappingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taskDateMapping);
    }

    /**
     * {@code DELETE  /task-date-mappings/:id} : delete the "id" taskDateMapping.
     *
     * @param id the id of the taskDateMapping to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/task-date-mappings/{id}")
    public ResponseEntity<Void> deleteTaskDateMapping(@PathVariable Long id) {
        log.debug("REST request to delete TaskDateMapping : {}", id);
        taskDateMappingService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/task-date-mappings?query=:query} : search for the taskDateMapping corresponding
     * to the query.
     *
     * @param query the query of the taskDateMapping search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/task-date-mappings")
    public ResponseEntity<List<TaskDateMapping>> searchTaskDateMappings(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of TaskDateMappings for query {}", query);
        Page<TaskDateMapping> page = taskDateMappingService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
