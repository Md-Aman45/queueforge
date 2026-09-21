package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class JobExecutorTest {

    @Test
    void shouldDefineExecutionContract() throws NoSuchMethodException {

        Class<JobExecutor> executorClass =
                JobExecutor.class;

        assertNotNull(
                executorClass.getMethod(
                        "supports",
                        String.class
                )
        );

        assertNotNull(
                executorClass.getMethod(
                        "execute",
                        JobExecutionMessage.class
                )
        );
    }
}