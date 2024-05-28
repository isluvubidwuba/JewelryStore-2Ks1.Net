package com.ks1dotnet.jewelrystore.exception;

public class RunTimeExceptionV1 extends RuntimeException {
    private String errorString;

    public RunTimeExceptionV1(String errorString, String message) {
        super(errorString + message == null ? "" : message);
        this.errorString = errorString;
    }

    public RunTimeExceptionV1(String errorString) {
        super(errorString);
        this.errorString = errorString;
    }

    public String getErrorString() {
        return errorString;
    }
}
