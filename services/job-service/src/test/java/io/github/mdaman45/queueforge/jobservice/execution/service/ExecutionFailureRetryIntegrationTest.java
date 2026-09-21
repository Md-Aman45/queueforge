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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ExecutionFailureRetryIntegrationTest {

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private ExecutionRepository executionRepository;

    @Autowired
    private JobRepository jobRepository;

    @Test
    void shouldCreateWaitingRetryExecutionWhenExecutionFails() {

        RetryPolicy retryPolicy = new RetryPolicy(
                3,
                true,
                5,
                BackoffStrategy.FIXED);

        Job job = new Job(
                "Failure Retry Integration Test",
                JobType.COMMUNICATION,
                JobStatus.ACCEPTED,
                retryPolicy);

        Job savedJob = jobRepository.save(job);

        Execution execution = new Execution(
                savedJob,
                ExecutionStatus.STARTED,
                1);

        execution.setStartedAt(Instant.now());

        Execution savedExecution = executionRepository.save(execution);

        executionService.startExecution(
                savedExecution.getId());

        Execution failedExecution = executionService.failExecution(
                savedExecution.getId());

        assertNotNull(failedExecution);

        assertEquals(
                ExecutionStatus.FAILED,
                failedExecution.getStatus());

        var executions = executionRepository.findByJob(savedJob);

        assertEquals(2, executions.size());

        Execution retryExecution = executions.stream()
                .filter(e -> e.getStatus() == ExecutionStatus.WAITING_FOR_RETRY)
                .findFirst()
                .orElse(null);

        assertNotNull(retryExecution);

        assertEquals(
                2,
                retryExecution.getAttemptNumber());

        assertNotNull(
                retryExecution.getNextAttemptAt());
    }
}