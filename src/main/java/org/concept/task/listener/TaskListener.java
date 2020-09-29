package org.concept.task.listener;

import org.concept.task.domain.Task;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

public interface TaskListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void onTaskChange(Task task);
}
