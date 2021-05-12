package com.eaProject.demo.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends Exception{
    private final HttpStatus status = HttpStatus.UNAUTHORIZED;

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
