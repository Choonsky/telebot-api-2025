package com.choonsky.telegrambot.exception;

public class UnknownActionException extends ApiException {
    public UnknownActionException(String s) {
        super("No such action: " + s + "!");
    }
}
