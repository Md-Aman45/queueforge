package io.github.mdaman45.queueforge.jobservice.job.dto;

import io.github.mdaman45.queueforge.jobservice.job.enums.JobStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateJobStatusRequest(

        @NotNull(message = "Job status is required")
        JobStatus status

) {
}