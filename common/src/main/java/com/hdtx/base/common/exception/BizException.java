package com.hdtx.base.common.exception;

/**
 * @author: ghx
 * @date 2021/8/13
 * @describe: 自定义参数异常
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private int resultCode;

    /**
     * 错误描述
     */
    private String resultMsg;

    public BizException(BaseErrorInfo errorInfo) {
        this.resultCode = errorInfo.getResultCode();
        this.resultMsg = errorInfo.getResultMsg();
    }

    public BizException(String resultMsg) {
        super(resultMsg);
        this.resultCode = CommonEnum.SYSTEM_ERROR.getResultCode();
        this.resultMsg = resultMsg;
    }

    public BizException(int resultCode, String resultMsg) {
        super(resultMsg);
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
