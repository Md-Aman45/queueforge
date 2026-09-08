package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.ExecutionResponse;
import io.github.mdaman45.queueforge.jobservice.execution.entity.Execution;
import io.github.mdaman45.queueforge.jobservice.execution.enums.ExecutionStatus;
import io.github.mdaman45.queueforge.jobservice.execution.repository.ExecutionRepository;
import io.github.mdaman45.queueforge.jobservice.execution.state.ExecutionStateMachine;
import io.github.mdaman45.queueforge.jobservice.exception.ResourceNotFoundException;
import io.github.mdaman45.queueforge.jobservice.job.entity.Job;
import io.github.mdaman45.queueforge.jobservice.job.repository.JobRepository;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ExecutionService {

    private final ExecutionRepository executionRepository;
    private final JobRepository jobRepository;
    private final ExecutionRetryService executionRetryService;

    public ExecutionService(
            ExecutionRepository executionRepository,
            JobRepository jobRepository,
            ExecutionRetryService executionRetryService
    ) {
        this.executionRepository = executionRepository;
        this.jobRepository = jobRepository;
        this.executionRetryService = executionRetryService;
    }

    public Execution createInitialExecution(Job job) {

        Execution execution = new Execution(
                job,
                ExecutionStatus.STARTED,
                1
        );

        execution.setStartedAt(Instant.now());

        return executionRepository.save(execution);
    }

    public Execution save(Execution execution) {
        return executionRepository.save(execution);
    }

    public ExecutionResponse getExecutionById(
            String executionId
    ) {

        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Execution not found with id: "
                                        + executionId
                        )
                );

        return new ExecutionResponse(
                execution.getId(),
                execution.getJob().getId(),
                execution.getStatus().name(),
                execution.getAttemptNumber()
        );
    }

    public ExecutionResponse startExecution(
            String executionId
    ) {

        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Execution not found with id: "
                                        + executionId
                        )
                );

        boolean validTransition =
                ExecutionStateMachine.isValidTransition(
                        execution.getStatus(),
                        ExecutionStatus.RUNNING
                );

        if (!validTransition) {
            throw new IllegalStateException(
                    "Execution cannot transition from "
                            + execution.getStatus()
                            + " to "
                            + ExecutionStatus.RUNNING
            );
        }

        execution.setStatus(ExecutionStatus.RUNNING);

        Execution updatedExecution =
                executionRepository.save(execution);

        return new ExecutionResponse(
                updatedExecution.getId(),
                updatedExecution.getJob().getId(),
                updatedExecution.getStatus().name(),
                updatedExecution.getAttemptNumber()
        );
    }

    public List<ExecutionResponse> getExecutionsByJobId(
            String jobId
    ) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id: " + jobId
                        )
                );

        return executionRepository.findByJob(job)
                .stream()
                .map(execution -> new ExecutionResponse(
                        execution.getId(),
                        execution.getJob().getId(),
                        execution.getStatus().name(),
                        execution.getAttemptNumber()
                ))
                .toList();
    }

    public ExecutionResponse completeExecution(
            String executionId
    ) {

        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Execution not found with id: "
                                        + executionId
                        )
                );

        boolean validTransition =
                ExecutionStateMachine.isValidTransition(
                        execution.getStatus(),
                        ExecutionStatus.SUCCEEDED
                );

        if (!validTransition) {
            throw new IllegalStateException(
                    "Execution cannot transition from "
                            + execution.getStatus()
                            + " to "
                            + ExecutionStatus.SUCCEEDED
            );
        }

        execution.setStatus(ExecutionStatus.SUCCEEDED);
        execution.setCompletedAt(Instant.now());

        Execution updatedExecution =
                executionRepository.save(execution);

        return new ExecutionResponse(
                updatedExecution.getId(),
                updatedExecution.getJob().getId(),
                updatedExecution.getStatus().name(),
                updatedExecution.getAttemptNumber()
        );
    }

    public Execution failExecution(
            String executionId
    ) {

        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Execution not found with id: "
                                        + executionId
                        )
                );

        boolean validTransition =
                ExecutionStateMachine.isValidTransition(
                        execution.getStatus(),
                        ExecutionStatus.FAILED
                );

        if (!validTransition) {
            throw new IllegalStateException(
                    "Execution cannot transition from "
                            + execution.getStatus()
                            + " to "
                            + ExecutionStatus.FAILED
            );
        }

        // Mark current execution as FAILED
        execution.setStatus(ExecutionStatus.FAILED);
        execution.setCompletedAt(Instant.now());

        Execution failedExecution =
                executionRepository.save(execution);

        // Let the retry service decide whether another attempt
        // should be created.
        executionRetryService.prepareRetry(
                failedExecution,
                failedExecution.getJob().getRetryPolicy()
        );

        return failedExecution;
    }
}