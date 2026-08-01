package io.github.mdaman45.queueforge.jobservice.job.dto;

import io.github.mdaman45.queueforge.jobservice.job.enums.JobType;

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
        JobType jobType

) {
}