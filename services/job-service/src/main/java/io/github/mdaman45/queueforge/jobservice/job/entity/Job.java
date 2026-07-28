package io.github.mdaman45.queueforge.jobservice.job.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "jobs")
public class Job {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String jobName;

    @Column(nullable = false)
    private String status;

    public Job() {
    }

    public Job(String jobName, String status) {
        this.jobName = jobName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
