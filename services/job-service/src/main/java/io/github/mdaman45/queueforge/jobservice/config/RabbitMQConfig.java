package io.github.mdaman45.queueforge.jobservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXECUTION_EXCHANGE =
            "queueforge.execution.exchange";

    public static final String EXECUTION_QUEUE =
            "queueforge.execution.queue";

    public static final String EXECUTION_ROUTING_KEY =
            "execution.dispatch";

    @Bean
    public TopicExchange executionExchange() {
        return new TopicExchange(EXECUTION_EXCHANGE);
    }

    @Bean
    public Queue executionQueue() {
        return new Queue(EXECUTION_QUEUE);
    }

    @Bean
    public Binding executionBinding(
            Queue executionQueue,
            TopicExchange executionExchange
    ) {
        return BindingBuilder
                .bind(executionQueue)
                .to(executionExchange)
                .with(EXECUTION_ROUTING_KEY);
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}