package com.hdtx.base.common.exception;

/**
 * @author: ghx
 * @date 2021/8/13
 * @describe:
 */
public enum CommonEnum implements BaseErrorInfo {

    // 数据操作错误定义
    SUCCESS(200, "成功!"),
    SYSTEM_ERROR(500, "系统异常"),
    METHOD_NOT_ALLOWED(403, "服务器拒绝响应"),
    BAD_METHOD(405, "请重新登陆"),
    NO_ACCESS(401, "无访问权限");

    /**
     * 错误码
     */
    private int resultCode;

    /**
     * 错误描述
     */
    private String resultMsg;

    CommonEnum(int resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public int getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}
