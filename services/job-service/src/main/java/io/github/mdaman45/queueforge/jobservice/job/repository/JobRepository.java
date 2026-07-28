package io.github.mdaman45.queueforge.jobservice.job.repository;

import io.github.mdaman45.queueforge.jobservice.job.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, String> {
}