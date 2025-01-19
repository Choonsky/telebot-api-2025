package com.choonsky.telegrambot.model;

public enum Severity {
    INFO,
    WARNING,
    ERROR,
    FATAL,
    PANIC,
    INCORRECT_SEVERITY;

    // this hides the Exception handling code
    // so you don't litter your code with try/catch blocks
    public static Severity safeValueOf(final String s)
    {
        try
        {
            return Severity.valueOf(s);
        }
        catch (final IllegalArgumentException e)
        {
            return INCORRECT_SEVERITY;
        }
    }
}
