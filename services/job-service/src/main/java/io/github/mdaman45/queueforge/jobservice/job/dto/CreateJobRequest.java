package io.github.mdaman45.queueforge.jobservice.job.dto;

import io.github.mdaman45.queueforge.jobservice.job.enums.JobType;
import io.github.mdaman45.queueforge.jobservice.retry.enums.BackoffStrategy;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateJobRequest(

        @NotBlank(message = "Job name is required")
        @Size(
                min = 3,
                max = 100,
                message = "Job name must be between 3 and 100 characters"
        )
        String jobName,

        @NotNull(message = "Job type is required")
        JobType jobType,

        @NotNull(message = "Max attempts is required")
        @Min(value = 1, message = "Max attempts must be at least 1")
        Integer maxAttempts,

        @NotNull(message = "Retryable is required")
        Boolean retryable,

        @NotNull(message = "Retry delay seconds is required")
        @Min(value = 0, message = "Retry delay seconds cannot be negative")
        Long retryDelaySeconds,

        @NotNull(message = "Backoff strategy is required")
        BackoffStrategy backoffStrategy

) {
}