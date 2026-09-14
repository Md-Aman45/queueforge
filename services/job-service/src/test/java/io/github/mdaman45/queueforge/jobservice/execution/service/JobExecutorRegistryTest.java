package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JobExecutorRegistryTest {

    @Test
    void shouldReturnCommunicationExecutor() {

        CommunicationJobExecutor communicationExecutor =
                new CommunicationJobExecutor();

        JobExecutorRegistry registry =
                new JobExecutorRegistry(
                        List.of(communicationExecutor)
                );

        JobExecutionMessage message =
                new JobExecutionMessage(
                        "execution-123",
                        "job-123",
                        1,
                        "COMMUNICATION"
                );

        JobExecutor result =
                registry.getExecutor(message);

        assertEquals(
                communicationExecutor,
                result
        );
    }

    @Test
    void shouldThrowExceptionWhenExecutorIsNotFound() {

        JobExecutorRegistry registry =
                new JobExecutorRegistry(List.of());

        JobExecutionMessage message =
                new JobExecutionMessage(
                        "execution-123",
                        "job-123",
                        1,
                        "AI"
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> registry.getExecutor(message)
        );
    }
}