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
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ExecutionService {

    private final ExecutionRepository executionRepository;
    private final JobRepository jobRepository;
    private final ExecutionRetryService executionRetryService;
    private final ExecutionDispatcher executionDispatcher;

    public ExecutionService(
            ExecutionRepository executionRepository,
            JobRepository jobRepository,
            ExecutionRetryService executionRetryService,
            ExecutionDispatcher executionDispatcher
    ) {
        this.executionRepository = executionRepository;
        this.jobRepository = jobRepository;
        this.executionRetryService = executionRetryService;
        this.executionDispatcher = executionDispatcher;
    }

    public Execution createInitialExecution(Job job) {

        Execution execution = new Execution(
                job,
                ExecutionStatus.STARTED,
                1
        );

        execution.setStartedAt(Instant.now());

        Execution savedExecution =
                executionRepository.save(execution);

        executionDispatcher.dispatch(savedExecution);

        return savedExecution;
    }

    public Execution save(Execution execution) {
        return executionRepository.save(execution);
    }

    public ExecutionResponse getExecutionById(
            String executionId
    ) {

        Execution execution =
                executionRepository.findById(executionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Execution not found with id: "
                                                + executionId
                        )
                        );

        return mapToResponse(execution);
    }

    @Transactional
    public ExecutionResponse startExecution(
            String executionId
    ) {

        Execution execution =
                executionRepository.findById(executionId)
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

        Execution savedExecution =
                executionRepository.save(execution);

        return mapToResponse(savedExecution);
    }

    public List<ExecutionResponse> getExecutionsByJobId(
            String jobId
    ) {

        Job job =
                jobRepository.findById(jobId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job not found with id: "
                                                + jobId
                                )
                        );

        return executionRepository.findByJob(job)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public ExecutionResponse completeExecution(
            String executionId
    ) {

        Execution execution =
                executionRepository.findById(executionId)
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

        Execution savedExecution =
                executionRepository.save(execution);

        return mapToResponse(savedExecution);
    }

    @Transactional
    public Execution failExecution(
            String executionId
    ) {

        Execution execution =
                executionRepository.findById(executionId)
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

        execution.setStatus(ExecutionStatus.FAILED);
        execution.setCompletedAt(Instant.now());

        Execution failedExecution =
                executionRepository.save(execution);

        executionRetryService.prepareRetry(
                failedExecution,
                failedExecution.getJob().getRetryPolicy()
        );

        return failedExecution;
    }

    private ExecutionResponse mapToResponse(
            Execution execution
    ) {

        return new ExecutionResponse(
                execution.getId(),
                execution.getJob().getId(),
                execution.getStatus().name(),
                execution.getAttemptNumber()
        );
    }
}