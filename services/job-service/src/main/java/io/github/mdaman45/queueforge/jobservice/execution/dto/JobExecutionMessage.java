package io.github.mdaman45.queueforge.jobservice.execution.dto;

public record JobExecutionMessage(
        String executionId,
        String jobId,
        Integer attemptNumber,
        String jobType
) {
}