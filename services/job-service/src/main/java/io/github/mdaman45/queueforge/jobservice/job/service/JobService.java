package io.github.mdaman45.queueforge.jobservice.job.service;

import io.github.mdaman45.queueforge.jobservice.common.response.ApiResponse;
import io.github.mdaman45.queueforge.jobservice.exception.ResourceNotFoundException;
import io.github.mdaman45.queueforge.jobservice.job.dto.CreateJobRequest;
import io.github.mdaman45.queueforge.jobservice.job.dto.CreateJobResponse;
import io.github.mdaman45.queueforge.jobservice.job.dto.JobResponse;
import io.github.mdaman45.queueforge.jobservice.job.dto.UpdateJobStatusRequest;
import io.github.mdaman45.queueforge.jobservice.job.entity.Job;
import io.github.mdaman45.queueforge.jobservice.job.repository.JobRepository;
import io.github.mdaman45.queueforge.jobservice.job.enums.JobStatus;
import io.github.mdaman45.queueforge.jobservice.job.state.JobStateMachine;
import io.github.mdaman45.queueforge.jobservice.exception.InvalidJobStateException;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // Creates Job...
    public ApiResponse<CreateJobResponse> createJob(CreateJobRequest request) {

        Job job = new Job(
                request.jobName(),
                request.jobType(),
                JobStatus.ACCEPTED
        );

        Job savedJob = jobRepository.save(job);

        CreateJobResponse response =
                new CreateJobResponse(
                        savedJob.getId(),
                        savedJob.getJobName(),
                        savedJob.getJobType().name(),
                        savedJob.getStatus().name()
                );

        return new ApiResponse<>(
            true,
            "Job accepted successfully",
            response,
            Instant.now()
        );
    }


    // Get Job by Id...
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
                job.getJobType().name(),
                job.getStatus().name()
        );
    }




    // Get all jobs...
    public List<JobResponse> getAllJobs() {

        return jobRepository.findAll()
                .stream()
                .map(job -> new JobResponse(
                        job.getId(),
                        job.getJobName(),
                        job.getJobType().name(),
                        job.getStatus().name()
                ))
                .toList();
    }





    // Update Job Staus...
    public JobResponse updateJobStatus(
        String jobId,
        UpdateJobStatusRequest request
    ) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id: " + jobId
                        ));


        if (!JobStateMachine.isValidTransition(
                job.getStatus(),
                request.status()
        )) {

                throw new InvalidJobStateException(
                        "Invalid status transition from "
                                + job.getStatus()
                                + " to "
                                + request.status()
                );
        }
        job.setStatus(request.status());

        Job updatedJob = jobRepository.save(job);

        return new JobResponse(
                updatedJob.getId(),
                updatedJob.getJobName(),
                updatedJob.getJobType().name(),
                updatedJob.getStatus().name()
        );
    } 
}
