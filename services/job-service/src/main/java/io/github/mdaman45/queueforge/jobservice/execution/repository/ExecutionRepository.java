package io.github.mdaman45.queueforge.jobservice.execution.repository;

import io.github.mdaman45.queueforge.jobservice.execution.entity.Execution;
import io.github.mdaman45.queueforge.jobservice.execution.enums.ExecutionStatus;
import io.github.mdaman45.queueforge.jobservice.job.entity.Job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface ExecutionRepository
        extends JpaRepository<Execution, String> {

    List<Execution> findByJob(Job job);

    List<Execution> findByStatusAndNextAttemptAtLessThanEqual(
            ExecutionStatus status,
            Instant currentTime
    );
}