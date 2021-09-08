package com.hdtx.base.common.exception;

import com.hdtx.base.common.api.CommonErrorCode;
import com.hdtx.base.common.api.ErrorCode;
import com.hdtx.base.common.api.ErrorInfo;
import com.netflix.hystrix.exception.HystrixBadRequestException;

/**
 * hystrix会忽略这个异常, 不会触发熔断
 * Created by liubin on 2016/5/3.
 */
public class RemoteCallException extends HystrixBadRequestException {

    private ErrorInfo originError;

    //类似Http状态码
    private int httpStatus = AppBusinessException.DEFAULT_CODE.getStatus();

    private CommonErrorCode commonErrorCode;

    private boolean systemException = false;

    public RemoteCallException(ErrorInfo error) {
        this(error, "");
    }

    public RemoteCallException(ErrorInfo error, String responseText) {
        super(String.format("调用远程服务异常, errorInfo[%s], respText[%s]", error.toString(), responseText));
        this.originError = error;
        this.httpStatus = error.getStatus();

        this.systemException = !ErrorCode.isBusinessStatus(this.httpStatus);
        if (this.systemException) {
            commonErrorCode = CommonErrorCode.fromHttpStatus(this.httpStatus);
        }
    }

    public ErrorInfo getOriginError() {
        return originError;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public CommonErrorCode getCommonErrorCode() {
        return commonErrorCode;
    }

    public boolean isSystemException() {
        return systemException;
    }
}
