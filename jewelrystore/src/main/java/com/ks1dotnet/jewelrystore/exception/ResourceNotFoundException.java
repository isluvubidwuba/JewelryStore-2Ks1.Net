package com.ks1dotnet.jewelrystore.exception;

public class ResourceNotFoundException extends RuntimeException {
    private String errorString;

    public ResourceNotFoundException(String errorString, String message) {
        super(errorString + message == null ? "" : message);
        this.errorString = errorString;
    }

    public ResourceNotFoundException(String errorString) {
        super(errorString);
        this.errorString = errorString;
    }

    public String getErrorString() {
        return errorString;
    }
}
