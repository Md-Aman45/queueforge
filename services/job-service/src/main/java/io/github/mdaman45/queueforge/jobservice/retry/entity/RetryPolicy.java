package io.github.mdaman45.queueforge.jobservice.retry.entity;

import io.github.mdaman45.queueforge.jobservice.retry.enums.BackoffStrategy;

import jakarta.persistence.*;

@Entity
@Table(name = "retry_policies")
public class RetryPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private int maxAttempts;

    @Column(nullable = false)
    private boolean retryable;

    @Column(nullable = false)
    private long retryDelaySeconds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BackoffStrategy backoffStrategy;

    public RetryPolicy() {
    }

    public RetryPolicy(
            int maxAttempts,
            boolean retryable,
            long retryDelaySeconds,
            BackoffStrategy backoffStrategy
    ) {
        this.maxAttempts = maxAttempts;
        this.retryable = retryable;
        this.retryDelaySeconds = retryDelaySeconds;
        this.backoffStrategy = backoffStrategy;
    }

    public String getId() {
        return id;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public boolean isRetryable() {
        return retryable;
    }

    public long getRetryDelaySeconds() {
        return retryDelaySeconds;
    }

    public BackoffStrategy getBackoffStrategy() {
        return backoffStrategy;
    }
}