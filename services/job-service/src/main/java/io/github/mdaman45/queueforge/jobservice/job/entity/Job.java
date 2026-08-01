package io.github.mdaman45.queueforge.jobservice.job.entity;

import io.github.mdaman45.queueforge.jobservice.job.enums.JobStatus;
import io.github.mdaman45.queueforge.jobservice.job.enums.JobType;

import jakarta.persistence.*;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String jobName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    public Job() {
    }

    public Job(String jobName, JobType jobType, JobStatus status) {
        this.jobName = jobName;
        this.jobType = jobType;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}