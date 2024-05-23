package com.ks1dotnet.jewelrystore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.ks1dotnet.jewelrystore.payload.ResponseData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseData> handleResourceNotFoundException(ResourceNotFoundException ex,
            WebRequest request) {
        log.error(ex.getMessage());
        ResponseData response = new ResponseData(HttpStatus.NOT_FOUND, ex.getErrorString(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseData> handleBadRequestException(BadRequestException ex, WebRequest request) {
        log.error(ex.getMessage());
        ResponseData response = new ResponseData(HttpStatus.BAD_REQUEST, ex.getErrorString(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RunTimeExceptionV1.class)
    public ResponseEntity<ResponseData> handleGlobalException(RunTimeExceptionV1 ex, WebRequest request) {
        log.error(ex.getMessage());
        ResponseData response = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, ex.getErrorString(), null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData> handleGlobalException(RuntimeException ex, WebRequest request) {
        log.error(ex.getMessage());
        ResponseData response = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
