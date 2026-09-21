package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.springframework.stereotype.Service;

@Service
public class CommunicationJobExecutor implements JobExecutor {

    @Override
    public boolean supports(String jobType) {
        return "COMMUNICATION".equals(jobType);
    }

    @Override
    public void execute(JobExecutionMessage message) {

        System.out.println(
                "Executing communication job: " + message
        );
    }
}