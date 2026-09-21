package io.github.mdaman45.queueforge.jobservice.retry.service;

import io.github.mdaman45.queueforge.jobservice.retry.entity.RetryPolicy;
import io.github.mdaman45.queueforge.jobservice.retry.enums.BackoffStrategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RetryBackoffServiceTest {

    private final RetryBackoffService service =
            new RetryBackoffService();

    @Test
    void shouldReturnFixedDelay() {

        RetryPolicy policy = new RetryPolicy(
                3,
                true,
                5,
                BackoffStrategy.FIXED
        );

        assertEquals(
                5,
                service.calculateDelaySeconds(policy, 1)
        );

        assertEquals(
                5,
                service.calculateDelaySeconds(policy, 2)
        );

        assertEquals(
                5,
                service.calculateDelaySeconds(policy, 3)
        );
    }

    @Test
    void shouldCalculateExponentialDelay() {

        RetryPolicy policy = new RetryPolicy(
                5,
                true,
                5,
                BackoffStrategy.EXPONENTIAL
        );

        assertEquals(
                5,
                service.calculateDelaySeconds(policy, 1)
        );

        assertEquals(
                10,
                service.calculateDelaySeconds(policy, 2)
        );

        assertEquals(
                20,
                service.calculateDelaySeconds(policy, 3)
        );

        assertEquals(
                40,
                service.calculateDelaySeconds(policy, 4)
        );
    }
}