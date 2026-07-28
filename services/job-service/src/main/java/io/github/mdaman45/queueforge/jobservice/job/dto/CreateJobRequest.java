package io.github.mdaman45.queueforge.jobservice.job.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateJobRequest(
        @NotBlank(message = "Job name is required")
        String jobName
) {
} 
