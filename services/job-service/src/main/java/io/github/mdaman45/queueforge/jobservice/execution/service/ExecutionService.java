package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.entity.Execution;
import io.github.mdaman45.queueforge.jobservice.execution.enums.ExecutionStatus;
import io.github.mdaman45.queueforge.jobservice.execution.repository.ExecutionRepository;
import io.github.mdaman45.queueforge.jobservice.job.entity.Job;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ExecutionService {

    private final ExecutionRepository executionRepository;

    public ExecutionService(ExecutionRepository executionRepository) {
        this.executionRepository = executionRepository;
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
}