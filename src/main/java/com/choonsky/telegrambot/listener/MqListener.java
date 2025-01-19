package com.choonsky.telegrambot.listener;

import com.choonsky.telegrambot.controller.TelegramBotController;
import com.choonsky.telegrambot.model.Headers;
import com.choonsky.telegrambot.model.TelebotMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
public class MqListener {

    private static final Logger logger = LoggerFactory.getLogger(MqListener.class);

    private final TelegramBotController telegramBotController;

    @Autowired
    public MqListener(TelegramBotController telegramBotController) {
        this.telegramBotController = telegramBotController;
    }

    @RabbitListener(queues = "${messaging.notifications.queues.telebot}")
    public void getMessage(TelebotMessage message) {
        logger.info("Received message from telebot_queue: {}", message);
        try {
            Headers headers = new Headers("1", "CINT_CALLER", "eng");
            ResponseEntity<String> response = telegramBotController.sendMessage(headers, message);
            logger.info("Notification sent to Telegram bot: {}", response.getBody());
        } catch (Exception e) {
            logger.error("Failed to send notification to Telegram bot for message: {} with error: {}", message, e.getMessage(), e);
        }
    }
}
