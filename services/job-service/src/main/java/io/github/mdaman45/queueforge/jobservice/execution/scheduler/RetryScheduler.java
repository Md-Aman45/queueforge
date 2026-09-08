package io.github.mdaman45.queueforge.jobservice.execution.scheduler;

import io.github.mdaman45.queueforge.jobservice.execution.service.RetryProcessor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RetryScheduler {

    private final RetryProcessor retryProcessor;

    public RetryScheduler(
            RetryProcessor retryProcessor
    ) {
        this.retryProcessor = retryProcessor;
    }

    @Scheduled(fixedDelay = 1000)
    public void processRetries() {
        retryProcessor.processDueRetries();
    }
}