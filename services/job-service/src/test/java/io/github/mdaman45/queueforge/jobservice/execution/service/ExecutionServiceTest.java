package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.entity.Execution;
import io.github.mdaman45.queueforge.jobservice.execution.enums.ExecutionStatus;
import io.github.mdaman45.queueforge.jobservice.execution.repository.ExecutionRepository;
import io.github.mdaman45.queueforge.jobservice.job.entity.Job;
import io.github.mdaman45.queueforge.jobservice.job.repository.JobRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutionServiceTest {

    @Mock
    private ExecutionRepository executionRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ExecutionRetryService executionRetryService;

    @Mock
    private ExecutionDispatcher executionDispatcher;

    private ExecutionService executionService;

    @BeforeEach
    void setUp() {

        executionService = new ExecutionService(
                executionRepository,
                jobRepository,
                executionRetryService,
                executionDispatcher
        );
    }

    @Test
    void shouldCreateExecutionService() {

        assertNotNull(executionService);
    }

    @Test
    void shouldDispatchInitialExecution() {

        Job job = new Job();

        Execution savedExecution =
                new Execution(
                        job,
                        ExecutionStatus.STARTED,
                        1
                );

        when(executionRepository.save(any(Execution.class)))
                .thenReturn(savedExecution);

        Execution result =
                executionService.createInitialExecution(job);

        assertNotNull(result);

        verify(executionDispatcher)
                .dispatch(savedExecution);
    }
}