package io.github.mdaman45.queueforge.jobservice.job.dto;

public record JobResponse(

        String jobId,

        String jobName,

        String jobType,

        String status

) {
}