package com.choonsky.telegrambot.exception;

public class WrongLangException extends ApiException {
    public WrongLangException(String s) {
        super("Wrong language header: " + s);
    }
}
