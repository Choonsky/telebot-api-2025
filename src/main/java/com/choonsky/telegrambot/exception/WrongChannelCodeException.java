package com.choonsky.telegrambot.exception;

public class WrongChannelCodeException extends ApiException {
    public WrongChannelCodeException(String s) {
        super("Wrong channel code header: " + s);
    }
}
