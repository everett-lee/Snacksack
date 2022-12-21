/*
package com.snacksack.snacksack.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
class GlobalDefaultExceptionHandler {
    private final static String FAILURE_MESSAGE_TEMPLATE = "Request failed due to: %s";

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleHTTPException(HttpClientErrorException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(String
                .format(FAILURE_MESSAGE_TEMPLATE, e.getMessage()));
        return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
    }

    @ExceptionHandler(MissingAuthTokenException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleMissingAuthException(Exception e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(String
                .format(FAILURE_MESSAGE_TEMPLATE, e.getMessage()));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(String
                .format(FAILURE_MESSAGE_TEMPLATE, e.getMessage()));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}*/
