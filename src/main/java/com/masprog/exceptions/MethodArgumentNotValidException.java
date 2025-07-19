package com.masprog.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MethodArgumentNotValidException extends RuntimeException {
    public MethodArgumentNotValidException() {
        super("Validation failed");
    }

    public MethodArgumentNotValidException(String message) {
        super(message);
    }

}
