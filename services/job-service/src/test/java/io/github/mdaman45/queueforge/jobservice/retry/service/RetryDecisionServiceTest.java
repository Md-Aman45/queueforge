package io.github.mdaman45.queueforge.jobservice.retry.service;

import io.github.mdaman45.queueforge.jobservice.retry.entity.RetryPolicy;
import io.github.mdaman45.queueforge.jobservice.retry.enums.BackoffStrategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RetryDecisionServiceTest {

    private final RetryDecisionService retryDecisionService =
            new RetryDecisionService();

    @Test
    void shouldNotRetryWhenRetryIsDisabled() {

        RetryPolicy policy =
                new RetryPolicy(
                        3,
                        false,
                        5,
                        BackoffStrategy.FIXED
                );

        assertFalse(
                retryDecisionService.shouldRetry(1, policy)
        );
    }

    @Test
    void shouldRetryWhenAttemptsAreRemaining() {

        RetryPolicy policy =
                new RetryPolicy(
                        3,
                        true,
                        5,
                        BackoffStrategy.FIXED
                );

        assertTrue(
                retryDecisionService.shouldRetry(1, policy)
        );
    }

    @Test
    void shouldRetryOnSecondAttemptWhenThirdIsAvailable() {

        RetryPolicy policy =
                new RetryPolicy(
                        3,
                        true,
                        5,
                        BackoffStrategy.EXPONENTIAL
                );

        assertTrue(
                retryDecisionService.shouldRetry(2, policy)
        );
    }

    @Test
    void shouldNotRetryWhenMaximumAttemptsReached() {

        RetryPolicy policy =
                new RetryPolicy(
                        3,
                        true,
                        5,
                        BackoffStrategy.EXPONENTIAL
                );

        assertFalse(
                retryDecisionService.shouldRetry(3, policy)
        );
    }

    @Test
    void shouldNotRetryWhenAttemptsExceedMaximum() {

        RetryPolicy policy =
                new RetryPolicy(
                        3,
                        true,
                        5,
                        BackoffStrategy.EXPONENTIAL
                );

        assertFalse(
                retryDecisionService.shouldRetry(4, policy)
        );
    }
}