package io.github.mdaman45.queueforge.jobservice.execution.service;

import io.github.mdaman45.queueforge.jobservice.execution.dto.JobExecutionMessage;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

// import static io.github.mdaman45.queueforge.jobservice.config.RabbitMQConfig.EXECUTION_EXCHANGE;
import static io.github.mdaman45.queueforge.jobservice.config.RabbitMQConfig.EXECUTION_ROUTING_KEY;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ExecutionMessageProducerIntegrationTest {

    @Autowired
    private ExecutionMessageProducer producer;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private org.springframework.amqp.core.TopicExchange executionExchange;

    @Test
    void shouldPublishMessageToRabbitMQ() {

        String testQueueName =
                "queueforge.execution.test." + UUID.randomUUID();

        Queue testQueue =
                new Queue(testQueueName, false, true, true);

        rabbitAdmin.declareQueue(testQueue);

        rabbitAdmin.declareBinding(
                BindingBuilder
                        .bind(testQueue)
                        .to(executionExchange)
                        .with(EXECUTION_ROUTING_KEY)
        );

        try {
            JobExecutionMessage message =
                    new JobExecutionMessage(
                            "integration-execution-123",
                            "integration-job-123",
                            1,
                            "COMMUNICATION"
                    );

            producer.publish(message);

            Object receivedMessage =
                    rabbitTemplate.receiveAndConvert(
                            testQueueName,
                            5000
                    );

            assertNotNull(receivedMessage);

        } finally {
            rabbitAdmin.deleteQueue(testQueueName);
        }
    }
}