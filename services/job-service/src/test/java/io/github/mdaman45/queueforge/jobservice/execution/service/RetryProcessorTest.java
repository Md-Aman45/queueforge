package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.entity.Execution;
import io.github.mdaman45.queueforge.jobservice.execution.enums.ExecutionStatus;
import io.github.mdaman45.queueforge.jobservice.execution.repository.ExecutionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetryProcessorTest {

    @Mock
    private ExecutionRepository executionRepository;

    @InjectMocks
    private RetryProcessor retryProcessor;

    @Test
    void shouldStartDueRetryExecution() {

        Execution execution = mock(Execution.class);

        when(executionRepository
                .findByStatusAndNextAttemptAtLessThanEqual(
                        eq(ExecutionStatus.WAITING_FOR_RETRY),
                        any(Instant.class)
                ))
                .thenReturn(List.of(execution));

        retryProcessor.processDueRetries();

        verify(execution)
                .setStatus(ExecutionStatus.STARTED);

        verify(execution)
                .setStartedAt(any(Instant.class));

        verify(execution)
                .setNextAttemptAt(null);

        verify(executionRepository)
                .save(execution);
    }

    @Test
    void shouldDoNothingWhenNoRetryIsDue() {

        when(executionRepository
                .findByStatusAndNextAttemptAtLessThanEqual(
                        eq(ExecutionStatus.WAITING_FOR_RETRY),
                        any(Instant.class)
                ))
                .thenReturn(List.of());

        retryProcessor.processDueRetries();

        verify(executionRepository, never())
                .save(any(Execution.class));
    }
}