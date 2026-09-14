package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExecutionOrchestratorTest {

    @Test
    void shouldExecuteJobUsingResolvedExecutor() {

        JobExecutorRegistry registry = mock(JobExecutorRegistry.class);

        JobExecutor executor = mock(JobExecutor.class);

        ExecutionOrchestrator orchestrator = new ExecutionOrchestrator(registry);

        JobExecutionMessage message = new JobExecutionMessage(
                "execution-123",
                "job-123",
                1,
                "COMMUNICATION");

        when(registry.getExecutor(message))
                .thenReturn(executor);

        orchestrator.execute(message);

        verify(registry)
                .getExecutor(message);

        verify(executor)
                .execute(message);
    }

    @Test
    void shouldPropagateExecutorFailure() {

        JobExecutorRegistry registry = mock(JobExecutorRegistry.class);

        JobExecutor executor = mock(JobExecutor.class);

        ExecutionOrchestrator orchestrator = new ExecutionOrchestrator(registry);

        JobExecutionMessage message = new JobExecutionMessage(
                "execution-456",
                "job-456",
                1,
                "COMMUNICATION");

        when(registry.getExecutor(message))
                .thenReturn(executor);

        RuntimeException failure = new RuntimeException("Job execution failed");

        org.mockito.Mockito
                .doThrow(failure)
                .when(executor)
                .execute(message);

        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> orchestrator.execute(message));

        verify(executor)
                .execute(message);
    }
}