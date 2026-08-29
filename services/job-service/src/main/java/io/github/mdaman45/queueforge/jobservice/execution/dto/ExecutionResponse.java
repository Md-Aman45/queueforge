package io.github.mdaman45.queueforge.jobservice.execution.dto;

public record ExecutionResponse(

        String executionId,

        String jobId,

        String status,

        int attemptNumber

) {
}