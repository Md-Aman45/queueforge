package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ExecutionMessageConsumerTest {

    @Test
    void shouldConsumeExecutionMessage() {

        ExecutionService executionService =
                mock(ExecutionService.class);

        ExecutionMessageConsumer consumer =
                new ExecutionMessageConsumer(executionService);

        JobExecutionMessage message =
                new JobExecutionMessage(
                        "test-execution-123",
                        "test-job-123",
                        1,
                        "COMMUNICATION"
                );

        consumer.consume(message);

        verify(executionService)
                .startExecution("test-execution-123");
    }
}