package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.entity.Execution;
import io.github.mdaman45.queueforge.jobservice.execution.enums.ExecutionStatus;
import io.github.mdaman45.queueforge.jobservice.execution.repository.ExecutionRepository;
import io.github.mdaman45.queueforge.jobservice.job.entity.Job;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ExecutionService {

    private final ExecutionRepository executionRepository;

    public ExecutionService(ExecutionRepository executionRepository) {
        this.executionRepository = executionRepository;
    }

    public Execution createInitialExecution(Job job) {

        Execution execution = new Execution(
                job,
                ExecutionStatus.STARTED,
                1
        );

        execution.setStartedAt(Instant.now());

        return executionRepository.save(execution);
    }

    public Execution completeExecution(Execution execution) {

        execution.setStatus(ExecutionStatus.SUCCEEDED);
        execution.setCompletedAt(Instant.now());

        return executionRepository.save(execution);
    }

    public Execution save(Execution execution) {
        return executionRepository.save(execution);
    }

    public Execution createRetryExecution(
            Execution failedExecution,
            long delaySeconds
    ) {

        int nextAttemptNumber =
                failedExecution.getAttemptNumber() + 1;

        Execution retryExecution = new Execution(
                failedExecution.getJob(),
                ExecutionStatus.WAITING_FOR_RETRY,
                nextAttemptNumber
        );

        retryExecution.setNextAttemptAt(
                Instant.now().plusSeconds(delaySeconds)
        );

        return executionRepository.save(retryExecution);
    }
}