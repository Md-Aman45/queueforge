package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.entity.Execution;
import io.github.mdaman45.queueforge.jobservice.execution.enums.ExecutionStatus;
import io.github.mdaman45.queueforge.jobservice.execution.repository.ExecutionRepository;
import io.github.mdaman45.queueforge.jobservice.execution.state.ExecutionStateMachine;
import io.github.mdaman45.queueforge.jobservice.retry.entity.RetryPolicy;
import io.github.mdaman45.queueforge.jobservice.retry.service.RetryBackoffService;
import io.github.mdaman45.queueforge.jobservice.retry.service.RetryDecisionService;

import org.springframework.stereotype.Service;

@Service
public class ExecutionRetryService {

    private final RetryDecisionService retryDecisionService;
    private final RetryBackoffService retryBackoffService;
    private final ExecutionRepository executionRepository;

    public ExecutionRetryService(
            RetryDecisionService retryDecisionService,
            RetryBackoffService retryBackoffService,
            ExecutionRepository executionRepository
    ) {
        this.retryDecisionService = retryDecisionService;
        this.retryBackoffService = retryBackoffService;
        this.executionRepository = executionRepository;
    }

    public Execution prepareRetry(
            Execution failedExecution,
            RetryPolicy retryPolicy
    ) {

        int currentAttempt = failedExecution.getAttemptNumber();

        boolean shouldRetry = retryDecisionService.shouldRetry(
                currentAttempt,
                retryPolicy
        );

        if (!shouldRetry) {
            return failedExecution;
        }

        boolean validTransition = ExecutionStateMachine.isValidTransition(
                failedExecution.getStatus(),
                ExecutionStatus.WAITING_FOR_RETRY
        );

        if (!validTransition) {
            throw new IllegalStateException(
                    "Execution cannot transition from "
                            + failedExecution.getStatus()
                            + " to "
                            + ExecutionStatus.WAITING_FOR_RETRY
            );
        }

        long delaySeconds =
                retryBackoffService.calculateDelaySeconds(
                        retryPolicy,
                        currentAttempt
                );

        return createRetryExecution(
                failedExecution,
                delaySeconds
        );
    }

    private Execution createRetryExecution(
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
                java.time.Instant.now().plusSeconds(delaySeconds)
        );

        return executionRepository.save(retryExecution);
    }
}