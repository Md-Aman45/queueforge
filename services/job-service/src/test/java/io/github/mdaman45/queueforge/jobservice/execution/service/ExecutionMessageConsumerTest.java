package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ExecutionMessageConsumerTest {

    @Test
    void shouldDelegateMessageToExecutionOrchestrator() {

        ExecutionOrchestrator executionOrchestrator =
                mock(ExecutionOrchestrator.class);

        ExecutionMessageConsumer consumer =
                new ExecutionMessageConsumer(
                        executionOrchestrator
                );

        JobExecutionMessage message =
                new JobExecutionMessage(
                        "test-execution-123",
                        "test-job-123",
                        1,
                        "COMMUNICATION"
                );

        consumer.consume(message);

        verify(executionOrchestrator)
                .execute(message);
    }
}