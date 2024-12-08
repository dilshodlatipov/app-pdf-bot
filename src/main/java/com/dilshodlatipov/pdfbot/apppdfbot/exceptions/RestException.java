package com.dilshodlatipov.pdfbot.apppdfbot.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class RestException extends RuntimeException {

    private HttpStatus status;
    private final Long userId;


    public RestException(String message, HttpStatus status, Long userId) {
        super(message);
        this.status = status;
        this.userId = userId;
    }

/*    public static RestException restThrow() {
        return new RestException(message, httpStatus, userId);
    }*/

    public static RestException restThrow(String message, Long userId) {
        return new RestException(message, HttpStatus.BAD_REQUEST, userId);
    }
}
