package com.choonsky.telegrambot;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info =
@Info(title = "Telegram Bot API Service", version = "1.0", description = "Telegram Bot API Service v1.0")
)
public class TelegramBotApi {

    public static void main(String[] args) {
        try {
            SpringApplication.run(TelegramBotApi.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
