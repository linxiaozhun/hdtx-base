package com.hdtx.base.apiutils.exception;

import com.hdtx.base.apiutils.api.CommonError;
import com.hdtx.base.apiutils.api.ErrorCode;

/**
 * @Author liubin
 * @Date 2017/5/15 10:04
 */
public class ServiceUnavailableException extends AppBusinessException {

    private static final ErrorCode ERROR_CODE = CommonError.SERVICE_UNAVAILABLE;

    public ServiceUnavailableException(String message) {
        super(ERROR_CODE, " 远程服务不可用: " + message);
    }

}
