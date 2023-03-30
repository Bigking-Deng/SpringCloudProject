package com.bigking.springcloud.pojo;


public enum ResponseStatus {
    success(200, "SUCCESS"),
    fallback(430, "Fallback"),
    failure(440, "FAIL");


    int code;
    String message;

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
