package com.wxsoft.teleconsultation.entity;

public class BaseResp<T> {

    private boolean status;

    private String message;

    private T data;

    public boolean isSuccess() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
