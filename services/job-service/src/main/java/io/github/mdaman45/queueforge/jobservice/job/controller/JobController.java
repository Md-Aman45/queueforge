package io.github.mdaman45.queueforge.jobservice.job.controller;

import io.github.mdaman45.queueforge.jobservice.common.response.ApiResponse;
import io.github.mdaman45.queueforge.jobservice.job.dto.CreateJobRequest;
import io.github.mdaman45.queueforge.jobservice.job.dto.CreateJobResponse;
import io.github.mdaman45.queueforge.jobservice.job.dto.JobResponse;
import io.github.mdaman45.queueforge.jobservice.job.dto.UpdateJobStatusRequest;
import io.github.mdaman45.queueforge.jobservice.job.service.JobService;
import jakarta.validation.Valid;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {
    
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateJobResponse>> createJob(
            @Valid 
            @RequestBody CreateJobRequest request) {

        ApiResponse<CreateJobResponse> response = jobService.createJob(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }





    @GetMapping
    public ResponseEntity<ApiResponse<List<JobResponse>>> getAllJobs() {

        List<JobResponse> jobs = jobService.getAllJobs();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Jobs retrieved successfully",
                        jobs,
                        Instant.now()
                )
        );
    }



    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(
            @PathVariable String jobId
    ) {

        JobResponse response = jobService.getJobById(jobId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Job retrieved successfully",
                        response,
                        Instant.now()
                )
        );
    }



    @PatchMapping("/{jobId}/status")
    public ResponseEntity<ApiResponse<JobResponse>> updateJobStatus(

        @PathVariable String jobId,

        @Valid
        @RequestBody UpdateJobStatusRequest request

    ) {

        JobResponse response = jobService.updateJobStatus(jobId, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Job status updated successfully",
                        response,
                        Instant.now()
                )
        );
    }
}
