package com.server.nodak.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    static final String postExchangeName = "post-exchange";
    static final String followExchangeName = "follow-exchange";

    public static final String postQueueName = "post-queue";
    public static final String followQueueName = "follow-queue";

    @Bean
    Queue postQueue() {
        return new Queue(postQueueName, false);
    }

    @Bean
    Queue followQueue() {
        return new Queue(followQueueName, false);
    }

    @Bean
    TopicExchange postExchange() {
        return new TopicExchange(postExchangeName);
    }

    @Bean
    TopicExchange followExchange() {
        return new TopicExchange(followExchangeName);
    }

    @Bean
    Binding postBinding(Queue postQueue, TopicExchange postExchange) {
        return BindingBuilder.bind(postQueue).to(postExchange).with("post.#");
    }

    @Bean
    Binding followBinding(Queue followQueue, TopicExchange followExchange) {
        return BindingBuilder.bind(followQueue).to(followExchange).with("follow.#");
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}