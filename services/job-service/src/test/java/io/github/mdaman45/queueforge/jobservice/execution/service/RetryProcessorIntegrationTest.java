package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.entity.Execution;
import io.github.mdaman45.queueforge.jobservice.execution.enums.ExecutionStatus;
import io.github.mdaman45.queueforge.jobservice.execution.repository.ExecutionRepository;
import io.github.mdaman45.queueforge.jobservice.job.entity.Job;
import io.github.mdaman45.queueforge.jobservice.job.enums.JobStatus;
import io.github.mdaman45.queueforge.jobservice.job.enums.JobType;
import io.github.mdaman45.queueforge.jobservice.job.repository.JobRepository;
import io.github.mdaman45.queueforge.jobservice.retry.entity.RetryPolicy;
import io.github.mdaman45.queueforge.jobservice.retry.enums.BackoffStrategy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class RetryProcessorIntegrationTest {

    @Autowired
    private RetryProcessor retryProcessor;

    @Autowired
    private ExecutionRepository executionRepository;

    @Autowired
    private JobRepository jobRepository;

    @Test
    void shouldStartDueRetryExecution() {

        RetryPolicy retryPolicy =
                new RetryPolicy(
                        3,
                        true,
                        5,
                        BackoffStrategy.FIXED
                );

        Job job =
                new Job(
                        "Retry Processor Integration Test",
                        JobType.COMMUNICATION,
                        JobStatus.ACCEPTED,
                        retryPolicy
                );

        Job savedJob =
                jobRepository.save(job);

        Execution retryExecution =
                new Execution(
                        savedJob,
                        ExecutionStatus.WAITING_FOR_RETRY,
                        2
                );

        retryExecution.setNextAttemptAt(
                Instant.now().minusSeconds(1)
        );

        Execution savedExecution =
                executionRepository.save(retryExecution);

        retryProcessor.processDueRetries();

        Execution updatedExecution =
                executionRepository
                        .findById(savedExecution.getId())
                        .orElseThrow();

        assertEquals(
                ExecutionStatus.STARTED,
                updatedExecution.getStatus()
        );

        assertNull(
                updatedExecution.getNextAttemptAt()
        );
    }
}