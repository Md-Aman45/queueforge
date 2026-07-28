package io.github.mdaman45.queueforge.jobservice.job.service;

import io.github.mdaman45.queueforge.jobservice.common.response.ApiResponse;
import io.github.mdaman45.queueforge.jobservice.exception.ResourceNotFoundException;
import io.github.mdaman45.queueforge.jobservice.job.dto.CreateJobRequest;
import io.github.mdaman45.queueforge.jobservice.job.dto.CreateJobResponse;
import io.github.mdaman45.queueforge.jobservice.job.dto.JobResponse;
import io.github.mdaman45.queueforge.jobservice.job.entity.Job;
import io.github.mdaman45.queueforge.jobservice.job.repository.JobRepository;


import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    
    public ApiResponse<CreateJobResponse> createJob(CreateJobRequest request) {

        Job job = new Job(
                request.jobName(),
                "ACCEPTED"
        );

        Job savedJob = jobRepository.save(job);

        CreateJobResponse response =
                new CreateJobResponse(
                        savedJob.getId(),
                        savedJob.getJobName(),
                        savedJob.getStatus()
                );

        return new ApiResponse<>(
            true,
            "Job accepted successfully",
            response,
            Instant.now()
        );
    }

    public JobResponse getJobById(String jobId) {

        Job job = jobRepository.findById(jobId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Job not found with id: " + jobId
                    )
            );

        return new JobResponse(
                job.getId(),
                job.getJobName(),
                job.getStatus()
        );
    }
}
