package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.entity.Execution;
import io.github.mdaman45.queueforge.jobservice.execution.enums.ExecutionStatus;
import io.github.mdaman45.queueforge.jobservice.execution.state.ExecutionStateMachine;
import io.github.mdaman45.queueforge.jobservice.retry.entity.RetryPolicy;
import io.github.mdaman45.queueforge.jobservice.retry.service.RetryBackoffService;
import io.github.mdaman45.queueforge.jobservice.retry.service.RetryDecisionService;

import org.springframework.stereotype.Service;

@Service
public class ExecutionRetryService {

    private final RetryDecisionService retryDecisionService;
    private final RetryBackoffService retryBackoffService;
    private final ExecutionService executionService;

    public ExecutionRetryService(
            RetryDecisionService retryDecisionService,
            RetryBackoffService retryBackoffService,
            ExecutionService executionService
    ) {
        this.retryDecisionService = retryDecisionService;
        this.retryBackoffService = retryBackoffService;
        this.executionService = executionService;
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

        failedExecution.setStatus(ExecutionStatus.WAITING_FOR_RETRY);

        long delaySeconds = retryBackoffService.calculateDelaySeconds(
                retryPolicy,
                currentAttempt
        );

        return executionService.createRetryExecution(
                failedExecution,
                delaySeconds
        );
    }
}