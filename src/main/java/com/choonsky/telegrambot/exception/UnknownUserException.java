package com.choonsky.telegrambot.exception;

public class UnknownUserException extends ApiException {
    public UnknownUserException(String s) {
        super("No such user: " + s + "!");
    }
}
