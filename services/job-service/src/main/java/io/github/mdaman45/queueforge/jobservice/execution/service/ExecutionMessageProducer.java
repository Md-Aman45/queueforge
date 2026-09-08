package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.config.RabbitMQConfig;
import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExecutionMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public ExecutionMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(JobExecutionMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXECUTION_EXCHANGE,
                RabbitMQConfig.EXECUTION_ROUTING_KEY,
                message
        );
    }
}