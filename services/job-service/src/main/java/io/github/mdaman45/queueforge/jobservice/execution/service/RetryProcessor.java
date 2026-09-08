package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.entity.Execution;
import io.github.mdaman45.queueforge.jobservice.execution.enums.ExecutionStatus;
import io.github.mdaman45.queueforge.jobservice.execution.repository.ExecutionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class RetryProcessor {

    private final ExecutionRepository executionRepository;

    public RetryProcessor(
            ExecutionRepository executionRepository
    ) {
        this.executionRepository = executionRepository;
    }

    @Transactional
    public void processDueRetries() {

        Instant now = Instant.now();

        List<Execution> dueExecutions =
                executionRepository
                        .findByStatusAndNextAttemptAtLessThanEqual(
                                ExecutionStatus.WAITING_FOR_RETRY,
                                now
                        );

        for (Execution execution : dueExecutions) {

            execution.setStatus(ExecutionStatus.STARTED);
            execution.setStartedAt(now);
            execution.setNextAttemptAt(null);

            executionRepository.save(execution);
        }
    }
}