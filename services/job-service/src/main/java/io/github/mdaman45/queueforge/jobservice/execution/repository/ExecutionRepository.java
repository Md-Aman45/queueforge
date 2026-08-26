package io.github.mdaman45.queueforge.jobservice.execution.repository;

import io.github.mdaman45.queueforge.jobservice.execution.entity.Execution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionRepository extends JpaRepository<Execution, String> {
}