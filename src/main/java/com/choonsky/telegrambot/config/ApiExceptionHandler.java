package com.choonsky.telegrambot.config;

import com.choonsky.telegrambot.controller.TelegramBotController;
import com.choonsky.telegrambot.exception.ApiException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice(assignableTypes = TelegramBotController.class)
@Slf4j
public class ApiExceptionHandler
        extends ResponseEntityExceptionHandler {

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
            log.error(String.format("HTTP responded with %s : %s", ApiException.CODE,
                    apiException.getMessage()));
        } else {
            response.setStatus(500);
            response.getWriter().write(exceptionToHandle.getMessage());
            log.error(String.format("HTTP responded with %s : %s", 500,
                    exceptionToHandle.getMessage()));
        }
        response.getWriter().flush();
    }

}
