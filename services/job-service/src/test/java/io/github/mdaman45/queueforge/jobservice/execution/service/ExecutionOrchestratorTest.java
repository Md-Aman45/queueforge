package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExecutionOrchestratorTest {

    @Test
    void shouldStartExecuteAndCompleteExecution() {

        ExecutionService executionService =
                mock(ExecutionService.class);

        JobExecutorRegistry registry =
                mock(JobExecutorRegistry.class);

        JobExecutor executor =
                mock(JobExecutor.class);

        ExecutionOrchestrator orchestrator =
                new ExecutionOrchestrator(
                        executionService,
                        registry
                );

        JobExecutionMessage message =
                new JobExecutionMessage(
                        "execution-123",
                        "job-123",
                        1,
                        "COMMUNICATION"
                );

        when(registry.getExecutor(message))
                .thenReturn(executor);

        orchestrator.execute(message);

        verify(executionService)
                .startExecution("execution-123");

        verify(registry)
                .getExecutor(message);

        verify(executor)
                .execute(message);

        verify(executionService)
                .completeExecution("execution-123");
    }

    @Test
    void shouldFailExecutionWhenExecutorThrowsException() {

        ExecutionService executionService =
                mock(ExecutionService.class);

        JobExecutorRegistry registry =
                mock(JobExecutorRegistry.class);

        JobExecutor executor =
                mock(JobExecutor.class);

        ExecutionOrchestrator orchestrator =
                new ExecutionOrchestrator(
                        executionService,
                        registry
                );

        JobExecutionMessage message =
                new JobExecutionMessage(
                        "execution-456",
                        "job-456",
                        1,
                        "COMMUNICATION"
                );

        when(registry.getExecutor(message))
                .thenReturn(executor);

        RuntimeException failure =
                new RuntimeException("Job execution failed");

        doThrow(failure)
                .when(executor)
                .execute(message);

        assertThrows(
                RuntimeException.class,
                () -> orchestrator.execute(message)
        );

        verify(executionService)
                .startExecution("execution-456");

        verify(executor)
                .execute(message);

        verify(executionService)
                .failExecution("execution-456");
    }
}