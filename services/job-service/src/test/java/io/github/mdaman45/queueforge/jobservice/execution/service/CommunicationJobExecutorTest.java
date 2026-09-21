package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CommunicationJobExecutorTest {

    @Test
    void shouldExecuteCommunicationJob() {

        CommunicationJobExecutor executor =
                new CommunicationJobExecutor();

        JobExecutionMessage message =
                new JobExecutionMessage(
                        "test-execution-123",
                        "test-job-123",
                        1,
                        "COMMUNICATION"
                );

        PrintStream originalOut = System.out;
        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(output));

            executor.execute(message);

            String consoleOutput =
                    output.toString();

            assertTrue(
                    consoleOutput.contains(
                            "test-execution-123"
                    )
            );

            assertTrue(
                    consoleOutput.contains(
                            "COMMUNICATION"
                    )
            );

        } finally {
            System.setOut(originalOut);
        }
    }
}