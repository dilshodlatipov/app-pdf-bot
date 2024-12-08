package com.dilshodlatipov.pdfbot.apppdfbot.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Slf4j
@RestControllerAdvice
@Order(value = Integer.MIN_VALUE)
@RequiredArgsConstructor
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExceptionHelper {

    @Value("${telegram.chat-id.log}")
    private Long CHAT_ID;

    @ExceptionHandler(value = {RestException.class})
    public ResponseEntity<BotApiMethod<?>> handleException(RestException ex, HttpServletRequest request) {
        logError(ex, request);
        return new ResponseEntity<>(
                SendMessage.builder().chatId(ex.getUserId()).text(ex.getMessage()).build(),
                HttpStatus.OK);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<BotApiMethod<?>> handleException(Exception ex, HttpServletRequest request) {
        logError(ex, request);
        return new ResponseEntity<>(
                SendMessage.builder().chatId(CHAT_ID).text(ex.getMessage()).build(),
                HttpStatus.OK);
    }

    private void logError(Exception ex, HttpServletRequest request) {
        String message = "";
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            message = wrapper.getContentAsString();
        }
        log.error("{} - {}", ex.getMessage(), message);
    }
}
