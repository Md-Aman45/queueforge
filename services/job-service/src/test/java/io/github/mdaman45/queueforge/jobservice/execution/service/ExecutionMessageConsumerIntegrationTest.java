package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;

import static io.github.mdaman45.queueforge.jobservice.config.RabbitMQConfig.EXECUTION_EXCHANGE;
import static io.github.mdaman45.queueforge.jobservice.config.RabbitMQConfig.EXECUTION_ROUTING_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ExecutionMessageConsumerIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ExecutionRepository executionRepository;

    @Test
    void shouldStartExecutionWhenMessageIsConsumed() {

        RetryPolicy retryPolicy = new RetryPolicy(
                3,
                true,
                5,
                BackoffStrategy.FIXED
        );

        Job job = new Job(
                "Consumer Integration Test",
                JobType.COMMUNICATION,
                JobStatus.ACCEPTED,
                retryPolicy
        );

        Job savedJob = jobRepository.save(job);

        Execution execution = new Execution(
                savedJob,
                ExecutionStatus.STARTED,
                1
        );

        execution.setStartedAt(Instant.now());

        Execution savedExecution =
                executionRepository.save(execution);

        JobExecutionMessage message =
                new JobExecutionMessage(
                        savedExecution.getId(),
                        savedJob.getId(),
                        savedExecution.getAttemptNumber(),
                        savedJob.getJobType().name()
                );

        rabbitTemplate.convertAndSend(
                EXECUTION_EXCHANGE,
                EXECUTION_ROUTING_KEY,
                message
        );

        Instant deadline =
                Instant.now().plus(Duration.ofSeconds(5));

        Execution updatedExecution;

        do {
            updatedExecution =
                    executionRepository
                            .findById(savedExecution.getId())
                            .orElse(null);

            if (updatedExecution != null
                    && updatedExecution.getStatus()
                    == ExecutionStatus.RUNNING) {
                break;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                throw new IllegalStateException(
                        "Test interrupted while waiting for execution",
                        e
                );
            }

        } while (Instant.now().isBefore(deadline));

        assertNotNull(updatedExecution);

        assertEquals(
                ExecutionStatus.RUNNING,
                updatedExecution.getStatus()
        );
    }
}