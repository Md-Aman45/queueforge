package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.entity.Execution;
import io.github.mdaman45.queueforge.jobservice.execution.enums.ExecutionStatus;
import io.github.mdaman45.queueforge.jobservice.job.entity.Job;
import io.github.mdaman45.queueforge.jobservice.job.enums.JobStatus;
import io.github.mdaman45.queueforge.jobservice.job.enums.JobType;
import io.github.mdaman45.queueforge.jobservice.retry.entity.RetryPolicy;
import io.github.mdaman45.queueforge.jobservice.retry.enums.BackoffStrategy;
import io.github.mdaman45.queueforge.jobservice.retry.service.RetryBackoffService;
import io.github.mdaman45.queueforge.jobservice.retry.service.RetryDecisionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExecutionRetryServiceTest {

    private RetryDecisionService retryDecisionService;
    private RetryBackoffService retryBackoffService;
    private ExecutionService executionService;

    private ExecutionRetryService executionRetryService;

    @BeforeEach
    void setUp() {

        retryDecisionService = mock(RetryDecisionService.class);
        retryBackoffService = mock(RetryBackoffService.class);
        executionService = mock(ExecutionService.class);

        executionRetryService = new ExecutionRetryService(
                retryDecisionService,
                retryBackoffService,
                executionService
        );
    }

    @Test
    void shouldPrepareRetryWhenRetryIsAllowed() {

        Job job = new Job(
                "test-job",
                JobType.COMMUNICATION,
                JobStatus.ACCEPTED,
                new RetryPolicy(
                        3,
                        true,
                        10,
                        BackoffStrategy.FIXED
                )
        );

        Execution failedExecution = new Execution(
                job,
                ExecutionStatus.FAILED,
                1
        );

        Execution retryExecution = new Execution(
                job,
                ExecutionStatus.STARTED,
                2
        );

        when(retryDecisionService.shouldRetry(1, job.getRetryPolicy()))
                .thenReturn(true);

        when(retryBackoffService.calculateDelaySeconds(
                job.getRetryPolicy(),
                1
        )).thenReturn(10L);

        when(executionService.createRetryExecution(
                failedExecution,
                10L
        )).thenReturn(retryExecution);

        Execution result = executionRetryService.prepareRetry(
                failedExecution,
                job.getRetryPolicy()
        );

        assertNotNull(result);
        assertEquals(ExecutionStatus.WAITING_FOR_RETRY,
                failedExecution.getStatus());

        assertEquals(ExecutionStatus.STARTED,
                result.getStatus());

        assertEquals(2, result.getAttemptNumber());

        verify(retryDecisionService)
                .shouldRetry(1, job.getRetryPolicy());

        verify(retryBackoffService)
                .calculateDelaySeconds(job.getRetryPolicy(), 1);

        verify(executionService)
                .createRetryExecution(failedExecution, 10L);
    }

    @Test
    void shouldNotRetryWhenRetryIsNotAllowed() {

        Job job = new Job(
                "test-job",
                JobType.COMMUNICATION,
                JobStatus.ACCEPTED,
                new RetryPolicy(
                        3,
                        false,
                        10,
                        BackoffStrategy.FIXED
                )
        );

        Execution failedExecution = new Execution(
                job,
                ExecutionStatus.FAILED,
                1
        );

        when(retryDecisionService.shouldRetry(1, job.getRetryPolicy()))
                .thenReturn(false);

        Execution result = executionRetryService.prepareRetry(
                failedExecution,
                job.getRetryPolicy()
        );

        assertSame(failedExecution, result);

        assertEquals(
                ExecutionStatus.FAILED,
                result.getStatus()
        );

        verify(retryDecisionService)
                .shouldRetry(1, job.getRetryPolicy());

        verifyNoInteractions(retryBackoffService);
        verifyNoInteractions(executionService);
    }

    @Test
    void shouldRejectRetryWhenExecutionIsNotFailed() {

        Job job = new Job(
                "test-job",
                JobType.COMMUNICATION,
                JobStatus.ACCEPTED,
                new RetryPolicy(
                        3,
                        true,
                        10,
                        BackoffStrategy.FIXED
                )
        );

        Execution execution = new Execution(
                job,
                ExecutionStatus.RUNNING,
                1
        );

        when(retryDecisionService.shouldRetry(1, job.getRetryPolicy()))
                .thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> executionRetryService.prepareRetry(
                        execution,
                        job.getRetryPolicy()
                )
        );

        verify(retryDecisionService)
                .shouldRetry(1, job.getRetryPolicy());

        verifyNoInteractions(retryBackoffService);
        verifyNoInteractions(executionService);
    }
}