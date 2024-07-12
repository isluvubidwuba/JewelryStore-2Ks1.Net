package com.ks1dotnet.jewelrystore.exception;

import org.springframework.http.HttpStatus;



public class ApplicationException extends RuntimeException {
    private String errorString;
    private HttpStatus status;

    public ApplicationException(String message, String errorString, HttpStatus status) {
        super(message);
        this.errorString = errorString;
        this.status = status;
    }

    public ApplicationException(String message, String errorString) {
        super(message);
        this.errorString = errorString;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ApplicationException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.errorString = "Something wrong happen!";
    }

    public ApplicationException(String errorString, HttpStatus status) {
        super("");
        this.errorString = errorString;
        this.status = status;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }


}
