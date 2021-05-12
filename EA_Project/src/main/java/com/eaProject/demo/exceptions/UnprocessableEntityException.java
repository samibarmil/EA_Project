package com.eaProject.demo.exceptions;

import org.springframework.http.HttpStatus;

public class UnprocessableEntityException extends Exception{
    private HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

    public HttpStatus getStatus() {
        return status;
    }

    public UnprocessableEntityException(String message) {
        super(message);
    }

}
