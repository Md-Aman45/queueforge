package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.config.RabbitMQConfig;
import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ExecutionMessageConsumer {

    private final ExecutionService executionService;

    public ExecutionMessageConsumer(
            ExecutionService executionService
    ) {
        this.executionService = executionService;
    }

    @RabbitListener(queues = RabbitMQConfig.EXECUTION_QUEUE)
    public void consume(JobExecutionMessage message) {

        executionService.startExecution(
                message.executionId()
        );
    }
}