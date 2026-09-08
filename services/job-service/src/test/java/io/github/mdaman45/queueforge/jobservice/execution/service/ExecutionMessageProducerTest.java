package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.config.RabbitMQConfig;
import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ExecutionMessageProducerTest {

    @Test
    void shouldPublishExecutionMessage() {

        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);

        ExecutionMessageProducer producer =
                new ExecutionMessageProducer(rabbitTemplate);

        JobExecutionMessage message = new JobExecutionMessage(
                "execution-123",
                "job-123",
                3,
                "COMMUNICATION"
        );

        producer.publish(message);

        verify(rabbitTemplate).convertAndSend(
                RabbitMQConfig.EXECUTION_EXCHANGE,
                RabbitMQConfig.EXECUTION_ROUTING_KEY,
                message
        );
    }
}