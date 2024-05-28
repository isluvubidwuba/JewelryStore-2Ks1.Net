package com.ks1dotnet.jewelrystore.exception;

public class BadRequestException extends RuntimeException {
    private String errorString;

    public BadRequestException(String errorString, String message) {
        super(errorString + (message == null ? "" : message));
        this.errorString = errorString;
    }

    public BadRequestException(String errorString) {
        super(errorString);
        this.errorString = errorString;
    }

    public String getErrorString() {
        return errorString;
    }

}
