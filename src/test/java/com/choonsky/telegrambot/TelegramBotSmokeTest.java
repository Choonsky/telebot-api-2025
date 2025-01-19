package com.choonsky.telegrambot;

import com.choonsky.telegrambot.controller.TelegramBotController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TelegramBotSmokeTest {

    @Autowired
    private TelegramBotController controller;

    @Test
    @DisplayName("Telegram Bot API service smoke test: context load and controller init")
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }

}
