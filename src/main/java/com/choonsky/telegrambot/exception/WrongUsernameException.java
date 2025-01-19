package com.choonsky.telegrambot.exception;

public class WrongUsernameException extends ApiException {
    public WrongUsernameException(String s) {
        super("Wrong username header: " + s);
    }
}
