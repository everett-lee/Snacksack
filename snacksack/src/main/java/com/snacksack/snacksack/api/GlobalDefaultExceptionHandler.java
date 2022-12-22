package com.snacksack.snacksack.api;

import com.snacksack.snacksack.api.exceptions.ExceptionResponse;
import com.snacksack.snacksack.api.exceptions.InvalidLocationException;
import com.snacksack.snacksack.api.exceptions.InvalidMoneyException;
import com.snacksack.snacksack.api.exceptions.RestaurantNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
class GlobalDefaultExceptionHandler {
    private final static String FAILURE_MESSAGE_TEMPLATE = "Request failed due to: %s";

    @ExceptionHandler(RestaurantNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleRestaurantNotFoundException(Exception e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(String
                .format(FAILURE_MESSAGE_TEMPLATE, e.getMessage()));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InvalidMoneyException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleInvalidMoneyException(Exception e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(String
                .format(FAILURE_MESSAGE_TEMPLATE, e.getMessage()));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidLocationException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleInvalidLocationException(Exception e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(String
                .format(FAILURE_MESSAGE_TEMPLATE, e.getMessage()));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse("Internal server error");
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
