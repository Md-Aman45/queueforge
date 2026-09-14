package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.springframework.stereotype.Service;

@Service
public class ExecutionOrchestrator {

    private final JobExecutorRegistry jobExecutorRegistry;

    public ExecutionOrchestrator(
            JobExecutorRegistry jobExecutorRegistry
    ) {
        this.jobExecutorRegistry = jobExecutorRegistry;
    }

    public void execute(JobExecutionMessage message) {

        JobExecutor executor =
                jobExecutorRegistry.getExecutor(message);

        executor.execute(message);
    }
}