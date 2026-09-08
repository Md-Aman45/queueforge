package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.github.mdaman45.queueforge.jobservice.config.RabbitMQConfig.EXECUTION_QUEUE;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ExecutionMessageProducerIntegrationTest {

    @Autowired
    private ExecutionMessageProducer producer;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void shouldPublishMessageToRabbitMQ() {

        JobExecutionMessage message = new JobExecutionMessage(
                "integration-execution-123",
                "integration-job-123",
                1,
                "COMMUNICATION"
        );

        producer.publish(message);

        Object receivedMessage =
                rabbitTemplate.receiveAndConvert(EXECUTION_QUEUE, 5000);

        assertNotNull(receivedMessage);
    }
}