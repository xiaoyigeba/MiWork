package com.example.finalwork.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "car-signal-queue";

    @Bean
    public DirectExchange carSignalExchange() {
        return new DirectExchange("car-signal-exchange");
    }

    @Bean
    public Queue carSignalQueue() {
        return new Queue("car-signal-queue", true);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("car-signal-key");
    }
}