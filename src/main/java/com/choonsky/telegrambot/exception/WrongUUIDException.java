package com.choonsky.telegrambot.exception;

public class WrongUUIDException extends ApiException {
    public WrongUUIDException(String s) {
        super("Wrong UUID: " + s + "!");
    }
}
