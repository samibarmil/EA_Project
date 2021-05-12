package com.eaProject.demo.advice;

import com.eaProject.demo.domain.ErrorData;
import com.eaProject.demo.exceptions.UnauthorizedAccessException;
import com.eaProject.demo.exceptions.UnprocessableEntityException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> buildResponseEntity(ErrorData errorData) {
        return new ResponseEntity<>(errorData, errorData.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            EntityNotFoundException ex) {
        ErrorData errorData = new ErrorData(NOT_FOUND);
        errorData.setMessage(ex.getMessage());
        return buildResponseEntity(errorData);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UnprocessableEntityException.class)
    protected ResponseEntity<Object> handleUnprocessableEntity(
            UnprocessableEntityException ex) {
        ErrorData errorData = new ErrorData(UNPROCESSABLE_ENTITY);
        errorData.setMessage(ex.getMessage());
        return buildResponseEntity(errorData);
    }


    @org.springframework.web.bind.annotation.ExceptionHandler(UnauthorizedAccessException.class)
    protected ResponseEntity<Object> handleUnauthorizedAccess(
            UnauthorizedAccessException ex) {
        ErrorData errorData = new ErrorData(UNAUTHORIZED);
        errorData.setMessage(ex.getMessage());
        return buildResponseEntity(errorData);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleOtherExceptions(
            Exception ex) {
        ErrorData errorData = new ErrorData(BAD_REQUEST);
        errorData.setMessage(ex.getMessage());
        return buildResponseEntity(errorData);
    }
}
