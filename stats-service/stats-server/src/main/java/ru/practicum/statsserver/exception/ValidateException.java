package ru.practicum.statsserver.exception;

import org.springframework.http.HttpStatus;

public class ValidateException extends RuntimeException {
    public ValidateException(String message) {
        super(message);
    }


    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}