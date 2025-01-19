package com.choonsky.telegrambot.exception;

public class WrongUserException extends ApiException {
    public WrongUserException(String userId, String customerId) {
        super("User ID = " + userId + " is not an active user of customer ID = " + customerId
        + "!");
    }
}
