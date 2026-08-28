package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.entity.Execution;
import io.github.mdaman45.queueforge.jobservice.execution.enums.ExecutionStatus;
import io.github.mdaman45.queueforge.jobservice.execution.repository.ExecutionRepository;
import io.github.mdaman45.queueforge.jobservice.job.entity.Job;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExecutionServiceTest {

    @Test
    void shouldCreateInitialExecution() {

        ExecutionRepository repository = mock(ExecutionRepository.class);
        ExecutionService service = new ExecutionService(repository);

        Job job = mock(Job.class);

        when(repository.save(any(Execution.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Execution result = service.createInitialExecution(job);

        assertNotNull(result);
        assertEquals(job, result.getJob());
        assertEquals(ExecutionStatus.STARTED, result.getStatus());
        assertEquals(1, result.getAttemptNumber());
        assertNotNull(result.getStartedAt());

        verify(repository, times(1))
                .save(any(Execution.class));
    }
}