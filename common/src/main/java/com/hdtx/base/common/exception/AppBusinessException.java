package com.hdtx.base.common.exception;

import com.hdtx.base.common.api.CommonErrorCode;
import com.hdtx.base.common.api.ErrorCode;

/**
 * @Author liubin
 * @Date 2017/5/15 10:34
 */
public class AppBusinessException extends BaseException {

    public static final ErrorCode DEFAULT_CODE = CommonErrorCode.INTERNAL_ERROR;


    //类似Http状态码
    private int httpStatus = DEFAULT_CODE.getStatus();

    private AppBusinessException(int httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    /**
     * 如果只设置message消息, 返回的http状态码将会是500
     * 根据新的接口规范, 业务异常不应该使用500状态码, 需要设置ErrorCode
     *
     * @param message
     */
//    @Deprecated
    public AppBusinessException(String message) {
        super(message);
    }

    /**
     * @param errorCode 状态码, 这个字段会在错误信息里返回给客户端.
     * @param message
     */
    public AppBusinessException(ErrorCode errorCode, String message) {
        this(errorCode.getStatus(), message);
    }

    public AppBusinessException(ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }


    public int getHttpStatus() {
        return httpStatus;
    }
}
