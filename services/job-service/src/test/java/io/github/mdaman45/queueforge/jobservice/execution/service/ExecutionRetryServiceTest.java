package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.entity.Execution;
import io.github.mdaman45.queueforge.jobservice.execution.enums.ExecutionStatus;
import io.github.mdaman45.queueforge.jobservice.execution.repository.ExecutionRepository;
import io.github.mdaman45.queueforge.jobservice.job.entity.Job;
import io.github.mdaman45.queueforge.jobservice.retry.entity.RetryPolicy;
import io.github.mdaman45.queueforge.jobservice.retry.service.RetryBackoffService;
import io.github.mdaman45.queueforge.jobservice.retry.service.RetryDecisionService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutionRetryServiceTest {

    @Mock
    private RetryDecisionService retryDecisionService;

    @Mock
    private RetryBackoffService retryBackoffService;

    @Mock
    private ExecutionRepository executionRepository;

    @Mock
    private Job job;

    @InjectMocks
    private ExecutionRetryService executionRetryService;

    @Test
    void shouldCreateRetryExecutionWhenRetryIsAllowed() {

        Execution failedExecution = mock(Execution.class);
        RetryPolicy retryPolicy = mock(RetryPolicy.class);
        Execution retryExecution = mock(Execution.class);

        when(failedExecution.getAttemptNumber())
                .thenReturn(1);

        when(failedExecution.getStatus())
                .thenReturn(ExecutionStatus.FAILED);

        when(failedExecution.getJob())
                .thenReturn(job);

        when(retryDecisionService.shouldRetry(
                1,
                retryPolicy
        )).thenReturn(true);

        when(retryBackoffService.calculateDelaySeconds(
                retryPolicy,
                1
        )).thenReturn(5L);

        when(executionRepository.save(any(Execution.class)))
                .thenReturn(retryExecution);

        Execution result =
                executionRetryService.prepareRetry(
                        failedExecution,
                        retryPolicy
                );

        assertSame(retryExecution, result);

        verify(retryDecisionService)
                .shouldRetry(1, retryPolicy);

        verify(retryBackoffService)
                .calculateDelaySeconds(
                        retryPolicy,
                        1
                );

        verify(executionRepository)
                .save(any(Execution.class));
    }

    @Test
    void shouldNotCreateRetryWhenRetryIsNotAllowed() {

        Execution failedExecution = mock(Execution.class);
        RetryPolicy retryPolicy = mock(RetryPolicy.class);

        when(failedExecution.getAttemptNumber())
                .thenReturn(5);

        when(retryDecisionService.shouldRetry(
                5,
                retryPolicy
        )).thenReturn(false);

        Execution result =
                executionRetryService.prepareRetry(
                        failedExecution,
                        retryPolicy
                );

        assertSame(failedExecution, result);

        verify(retryDecisionService)
                .shouldRetry(5, retryPolicy);

        verifyNoInteractions(retryBackoffService);
        verifyNoInteractions(executionRepository);
    }

    @Test
    void shouldNotModifyFailedExecutionWhenCreatingRetry() {

        Execution failedExecution = mock(Execution.class);
        RetryPolicy retryPolicy = mock(RetryPolicy.class);

        when(failedExecution.getAttemptNumber())
                .thenReturn(1);

        when(retryDecisionService.shouldRetry(
                1,
                retryPolicy
        )).thenReturn(false);

        executionRetryService.prepareRetry(
                failedExecution,
                retryPolicy
        );

        verify(failedExecution, never())
                .setStatus(ExecutionStatus.WAITING_FOR_RETRY);
    }
}