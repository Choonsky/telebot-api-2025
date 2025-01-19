package com.choonsky.telegrambot.exception;

public class UnknownSeverityException extends ApiException {
    public UnknownSeverityException(String s) {
        super("No such severity: " + s + "!");
    }
}
