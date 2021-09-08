package com.hdtx.base.common.exception;


import com.hdtx.base.common.api.CommonErrorCode;
import com.hdtx.base.common.api.ErrorCode;

/**
 * @Author liubin
 * @Date 2017/5/15 10:04
 */
public class ServiceUnavailableException extends AppBusinessException {

    private static final ErrorCode ERROR_CODE = CommonErrorCode.SERVICE_UNAVAILABLE;

    public ServiceUnavailableException(String message) {
        super(ERROR_CODE, " 远程服务不可用: " + message);
    }

}
