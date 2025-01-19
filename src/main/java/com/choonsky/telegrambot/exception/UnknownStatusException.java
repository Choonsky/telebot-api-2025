package com.choonsky.telegrambot.exception;

public class UnknownStatusException extends ApiException {
    public UnknownStatusException(String s) {
        super("No such file upload status: " + s + "!");
    }
}
