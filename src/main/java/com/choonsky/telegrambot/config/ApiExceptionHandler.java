package com.choonsky.telegrambot.config;

import jakarta.servlet.http.HttpServletResponse;
import com.choonsky.telegrambot.controller.TelegramBotController;
import com.choonsky.telegrambot.exception.ApiException;
import com.choonsky.telegrambot.exception.WrongChannelCodeException;
import com.choonsky.telegrambot.exception.WrongLangException;
import com.choonsky.telegrambot.exception.WrongUsernameException;
import com.choonsky.telegrambot.model.Headers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice(assignableTypes = TelegramBotController.class)
public class ApiExceptionHandler
        extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger("com.choonsky.telegrambot");

    @ExceptionHandler(Exception.class)
    static void handleException(Exception ex, HttpServletResponse response) throws IOException {

        Throwable exceptionToHandle = ex;

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        while (exceptionToHandle.getCause() != null) {
            exceptionToHandle = exceptionToHandle.getCause();
        }
        if (exceptionToHandle instanceof ApiException apiException) {
            response.setStatus(ApiException.CODE);
            response.getWriter().write(apiException.getMessage());
            LOGGER.error(String.format("HTTP responded with %s : %s", ApiException.CODE,
                    apiException.getMessage()));
        } else {
            response.setStatus(500);
            response.getWriter().write(exceptionToHandle.getMessage());
            LOGGER.error(String.format("HTTP responded with %s : %s", 500,
                    exceptionToHandle.getMessage()));
        }
        response.getWriter().flush();
    }

}
