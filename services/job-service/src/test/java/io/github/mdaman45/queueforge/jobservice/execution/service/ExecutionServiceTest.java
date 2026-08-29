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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExecutionServiceTest {

    @Test
    void createInitialExecutionShouldCreateStartedExecution() {

        ExecutionRepository executionRepository =
                mock(ExecutionRepository.class);

        JobRepository jobRepository =
                mock(JobRepository.class);

        ExecutionService executionService =
                new ExecutionService(
                        executionRepository,
                        jobRepository
                );

        RetryPolicy retryPolicy = new RetryPolicy(
                3,
                false,
                0,
                BackoffStrategy.FIXED
        );

        Job job = new Job(
                "Test Job",
                JobType.SYSTEM,
                JobStatus.ACCEPTED,
                retryPolicy
        );

        Execution savedExecution = new Execution(
                job,
                ExecutionStatus.STARTED,
                1
        );

        when(executionRepository.save(
                org.mockito.ArgumentMatchers.any(Execution.class)
        )).thenReturn(savedExecution);

        Execution result =
                executionService.createInitialExecution(job);

        assertEquals(
                ExecutionStatus.STARTED,
                result.getStatus()
        );

        assertEquals(
                1,
                result.getAttemptNumber()
        );
    }
}