package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

public interface JobExecutor {

    boolean supports(String jobType);

    void execute(JobExecutionMessage message);
}