package org.concept.task.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.concept.task.web.rest.TestUtil;

public class TaskDateMappingTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskDateMapping.class);
        TaskDateMapping taskDateMapping1 = new TaskDateMapping();
        taskDateMapping1.setId(1L);
        TaskDateMapping taskDateMapping2 = new TaskDateMapping();
        taskDateMapping2.setId(taskDateMapping1.getId());
        assertThat(taskDateMapping1).isEqualTo(taskDateMapping2);
        taskDateMapping2.setId(2L);
        assertThat(taskDateMapping1).isNotEqualTo(taskDateMapping2);
        taskDateMapping1.setId(null);
        assertThat(taskDateMapping1).isNotEqualTo(taskDateMapping2);
    }
}
