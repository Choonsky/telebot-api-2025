package com.choonsky.telegrambot.listener;

import com.choonsky.telegrambot.controller.TelegramBotController;
import com.choonsky.telegrambot.model.TelebotMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@Service
@Slf4j
@RestController
public class MqListener {

    private final TelegramBotController telegramBotController;

    @Autowired
    public MqListener(TelegramBotController telegramBotController) {
        this.telegramBotController = telegramBotController;
    }

    @RabbitListener(queues = "${messaging.notifications.queues.telebot}")
    public void getMessage(TelebotMessage message) {
        log.info("Received message from telebot_queue: {}", message);
        try {
            ResponseEntity<String> response = telegramBotController.sendMessage(message);
            log.info("Notification sent to Telegram bot: {}", response.getBody());
        } catch (Exception e) {
            log.error("Failed to send notification to Telegram bot for message: {} with error: {}", message, e.getMessage(), e);
        }
    }
}
