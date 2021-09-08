package com.hdtx.base.apiutils;

import com.hdtx.base.apiutils.api.CommonError;

/**
 * @author huangwenc
 */
public class ScResp<T> {

    private String message;
    private int status;
    private T result;

    public static <T> ScResp<T> create(String message, int status, T result) {
        ScResp<T> authResult = new ScResp<>();
        authResult.setResult(result);
        authResult.setMessage(message);
        authResult.setStatus(status);
        return authResult;
    }

    public static <T> ScResp<T> create(String message, int status) {
        ScResp<T> authResult = new ScResp<>();
        authResult.setMessage(message);
        authResult.setStatus(status);
        return authResult;
    }

    public static <T> ScResp<T> ok(T result) {
        return create("OK", Constants.HTTP_STATUS_OK, result);
    }

    public static ScResp internalError() {
        return create(CommonError.INTERNAL_ERROR.getMessage(), CommonError.INTERNAL_ERROR.getStatus());
    }

    public boolean isOk() {
        return this.status == Constants.HTTP_STATUS_OK;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
