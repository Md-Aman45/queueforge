package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;
import io.github.mdaman45.queueforge.jobservice.execution.entity.Execution;

import org.springframework.stereotype.Service;

@Service
public class RabbitMQExecutionDispatcher implements ExecutionDispatcher {

    private final ExecutionMessageProducer executionMessageProducer;

    public RabbitMQExecutionDispatcher(
            ExecutionMessageProducer executionMessageProducer
    ) {
        this.executionMessageProducer = executionMessageProducer;
    }

    @Override
    public void dispatch(Execution execution) {

        JobExecutionMessage message =
                new JobExecutionMessage(
                        execution.getId(),
                        execution.getJob().getId(),
                        execution.getAttemptNumber(),
                        execution.getJob().getJobType().name()
                );

        executionMessageProducer.publish(message);
    }
}