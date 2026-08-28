package io.github.mdaman45.queueforge.jobservice.retry.service;

import io.github.mdaman45.queueforge.jobservice.retry.entity.RetryPolicy;

import org.springframework.stereotype.Service;

@Service
public class RetryDecisionService {

    public boolean shouldRetry(
            int currentAttempt,
            RetryPolicy retryPolicy
    ) {

        if (!retryPolicy.isRetryable()) {
            return false;
        }

        return currentAttempt < retryPolicy.getMaxAttempts();
    }
}