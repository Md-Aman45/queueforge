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

    public ExecutionService(
            ExecutionRepository executionRepository,
            JobRepository jobRepository
    ) {
        this.executionRepository = executionRepository;
        this.jobRepository = jobRepository;
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


    public Execution createRetryExecution(
            Execution failedExecution,
            long delaySeconds
    ) {

        int nextAttemptNumber =
                failedExecution.getAttemptNumber() + 1;

        Execution retryExecution = new Execution(
                failedExecution.getJob(),
                ExecutionStatus.WAITING_FOR_RETRY,
                nextAttemptNumber
        );

        retryExecution.setNextAttemptAt(
                Instant.now().plusSeconds(delaySeconds)
        );

        return executionRepository.save(retryExecution);
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


    public Execution failExecution(String executionId) {

        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Execution not found with id: " + executionId
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

        execution.setStatus(ExecutionStatus.FAILED);
        execution.setCompletedAt(Instant.now());

        return executionRepository.save(execution);
    }
}