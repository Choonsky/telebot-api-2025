package com.choonsky.telegrambot.exception;

public class TelebotException extends ApiException {
    public TelebotException(String s) {
        super("Telegram API exception: " + s + "!");
    }
}