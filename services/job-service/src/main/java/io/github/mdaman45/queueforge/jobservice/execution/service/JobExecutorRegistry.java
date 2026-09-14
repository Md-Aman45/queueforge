package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobExecutorRegistry {

    private final List<JobExecutor> executors;

    public JobExecutorRegistry(List<JobExecutor> executors) {
        this.executors = executors;
    }

    public JobExecutor getExecutor(JobExecutionMessage message) {

        return executors.stream()
                .filter(executor ->
                        executor.supports(message.jobType())
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No executor found for job type: "
                                        + message.jobType()
                        )
                );
    }
}