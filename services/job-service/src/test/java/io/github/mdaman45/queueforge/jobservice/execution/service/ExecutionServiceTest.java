package io.github.mdaman45.queueforge.jobservice.execution.service;


import io.github.mdaman45.queueforge.jobservice.execution.repository.ExecutionRepository;
import io.github.mdaman45.queueforge.jobservice.job.repository.JobRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ExecutionServiceTest {

    @Mock
    private ExecutionRepository executionRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ExecutionRetryService executionRetryService;

    private ExecutionService executionService;

    @BeforeEach
    void setUp() {

        executionService = new ExecutionService(
                executionRepository,
                jobRepository,
                executionRetryService
        );
    }

    @Test
    void shouldCreateExecutionService() {

        assertNotNull(executionService);
    }
}