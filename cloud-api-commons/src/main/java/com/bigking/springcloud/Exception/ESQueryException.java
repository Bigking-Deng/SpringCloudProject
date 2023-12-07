package com.bigking.springcloud.Exception;

public class ESQueryException extends Exception{
    private final String esQueryExceptionMessage;

    public ESQueryException(String message) {
        super(message);
        this.esQueryExceptionMessage = message;
    }

    public ESQueryException(String message, Throwable cause) {
        super(message, cause);
        this.esQueryExceptionMessage = message;
    }

    public ESQueryException(Throwable cause) {
        super(cause);
        this.esQueryExceptionMessage = "";
    }

    @Override
    public String getMessage() {
        return this.esQueryExceptionMessage;
    }
}
