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

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ResponseData> handleApplicationException(ApplicationException ex,
            WebRequest request) {
        if (!ex.getMessage().isEmpty()) {
            log.error(ex.getMessage());
            log.error("Cause: ", ex.getCause());
        }
        ResponseData response = new ResponseData(ex.getStatus(), ex.getErrorString(), null);
        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData> handleGlobalException(RuntimeException ex,
            WebRequest request) {
        if (!ex.getMessage().isEmpty()) {
            log.error(ex.getMessage());
            log.error("Cause: ", ex.getCause());
        }
        ResponseData response =
                new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
