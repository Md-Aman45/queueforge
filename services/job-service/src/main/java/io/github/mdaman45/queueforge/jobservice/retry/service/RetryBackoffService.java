package io.github.mdaman45.queueforge.jobservice.retry.service;

import io.github.mdaman45.queueforge.jobservice.retry.entity.RetryPolicy;
import io.github.mdaman45.queueforge.jobservice.retry.enums.BackoffStrategy;

import org.springframework.stereotype.Service;

@Service
public class RetryBackoffService {

    public long calculateDelaySeconds(
            RetryPolicy retryPolicy,
            int attemptNumber
    ) {

        if (retryPolicy.getBackoffStrategy() == BackoffStrategy.FIXED) {
            return retryPolicy.getRetryDelaySeconds();
        }

        if (retryPolicy.getBackoffStrategy() == BackoffStrategy.EXPONENTIAL) {
            return retryPolicy.getRetryDelaySeconds()
                    * (1L << (attemptNumber - 1));
        }

        throw new IllegalArgumentException(
                "Unsupported backoff strategy: "
                        + retryPolicy.getBackoffStrategy()
        );
    }
}