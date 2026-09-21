package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.config.RabbitMQConfig;
import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ExecutionMessageConsumer {

    private final ExecutionOrchestrator executionOrchestrator;

    public ExecutionMessageConsumer(
            ExecutionOrchestrator executionOrchestrator
    ) {
        this.executionOrchestrator = executionOrchestrator;
    }

    @RabbitListener(queues = RabbitMQConfig.EXECUTION_QUEUE)
    public void consume(JobExecutionMessage message) {
        executionOrchestrator.execute(message);
    }
}