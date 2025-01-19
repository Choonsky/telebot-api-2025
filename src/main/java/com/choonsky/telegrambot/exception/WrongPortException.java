package com.choonsky.telegrambot.exception;

public class WrongPortException extends ApiException {
    public WrongPortException(String s) {
        super("No such port: " + s + "!");
    }
}
