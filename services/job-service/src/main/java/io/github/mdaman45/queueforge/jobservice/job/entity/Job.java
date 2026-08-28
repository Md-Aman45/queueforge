package io.github.mdaman45.queueforge.jobservice.job.entity;

import io.github.mdaman45.queueforge.jobservice.job.enums.JobStatus;
import io.github.mdaman45.queueforge.jobservice.job.enums.JobType;
import io.github.mdaman45.queueforge.jobservice.retry.entity.RetryPolicy;

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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    @JoinColumn(name = "retry_policy_id", nullable = false, unique = true)
    private RetryPolicy retryPolicy;

    public Job() {
    }

    public Job(String jobName, JobType jobType, JobStatus status, RetryPolicy retryPolicy) {
        this.jobName = jobName;
        this.jobType = jobType;
        this.status = status;
        this.retryPolicy = retryPolicy;
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

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }
}