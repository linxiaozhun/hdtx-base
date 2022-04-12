package com.hdtx.base.common.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by liubin on 15-8-3.
 */
public class ErrorInfo {

    private int code;

    private String message;

    private String requestUri;

    private int status;

    @Deprecated
    @JsonCreator
    public ErrorInfo(@JsonProperty("code") int code,
                     @JsonProperty("requestUri") String requestUri,
                     @JsonProperty(value = "message", defaultValue = "") String message) {
        this(code, requestUri, message, 500);
    }

    public ErrorInfo(ErrorCode errorCode, String requestUri) {
        this(errorCode, requestUri, null);
    }

    public ErrorInfo(ErrorCode errorCode, String requestUri, String message) {
        this(errorCode.getCode(), requestUri, message == null ? errorCode.getMessage() : message, errorCode.getStatus());
    }

    @Deprecated
    public ErrorInfo(int code, String requestUri, String message, int status) {
        this.code = code;
        this.requestUri = requestUri;
        this.message = message;
        this.status = status;
    }

    public ErrorInfo(String code, String requestUri, String message, int status) {
        this.code = status;
        this.requestUri = requestUri;
        this.message = message;
        this.status = status;
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public int getStatus() {
        return status;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ErrorInfo{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", requestUri='" + requestUri + '\'' +
                ", status=" + status +
                '}';
    }
}
