package io.github.mdaman45.queueforge.jobservice.job.service;

import io.github.mdaman45.queueforge.jobservice.common.response.ApiResponse;
import io.github.mdaman45.queueforge.jobservice.job.dto.CreateJobRequest;
import io.github.mdaman45.queueforge.jobservice.job.dto.CreateJobResponse;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JobService {
    
    public ApiResponse<CreateJobResponse> createJob(CreateJobRequest request) {

        CreateJobResponse response =
                new CreateJobResponse(
                        "JOB-001",
                        request.jobName(),
                        "ACCEPTED"
                );

        return new ApiResponse<>(
            true,
            "Job accepted successfully",
            response,
            Instant.now()
        );
    }
}
