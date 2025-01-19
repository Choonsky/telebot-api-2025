package com.choonsky.telegrambot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class ConfigMQ {

    @Value("${messaging.notifications.exchange.telegramBot}")
    private String EXCHANGE_TELEGRAM_BOT;

    @Value("${messaging.notifications.routingKeys.telegramBot}")
    private String TELEBOT_ROUTING_KEY;

    @Value("${messaging.notifications.queues.telebot}")
    private String TELEBOT_QUEUE;

    // Define RabbitMQ queues
    @Bean
    public Queue telebotQueue() {
        return new Queue(TELEBOT_QUEUE, true);
    }

    // Direct exchange
    @Bean
    public DirectExchange exchangeTelegramBot() {
        return new DirectExchange(EXCHANGE_TELEGRAM_BOT);
    }

    // Queue bindings
    @Bean
    public Binding bindingTelebotQueue(Queue telebotQueue, DirectExchange exchangeTelegramBot) {
        return BindingBuilder.bind(telebotQueue).to(exchangeTelegramBot).with(TELEBOT_ROUTING_KEY);
    }

    // Message converter for RabbitMQ to use JSON for message serialization
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // Configure RabbitTemplate with the JSON message converter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }

    // RabbitAdmin to manage exchanges and queues
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

}
