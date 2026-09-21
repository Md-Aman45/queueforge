package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.springframework.stereotype.Service;

@Service
public class ExecutionOrchestrator {

    private final ExecutionService executionService;
    private final JobExecutorRegistry jobExecutorRegistry;

    public ExecutionOrchestrator(
            ExecutionService executionService,
            JobExecutorRegistry jobExecutorRegistry
    ) {
        this.executionService = executionService;
        this.jobExecutorRegistry = jobExecutorRegistry;
    }

    public void execute(JobExecutionMessage message) {

        executionService.startExecution(
                message.executionId()
        );

        JobExecutor executor =
                jobExecutorRegistry.getExecutor(message);

        try {
            executor.execute(message);

            executionService.completeExecution(
                    message.executionId()
            );

        } catch (Exception exception) {

            executionService.failExecution(
                    message.executionId()
            );

            throw exception;
        }
    }
}