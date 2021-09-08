package com.hdtx.base.common.model;


import cn.hutool.json.JSONUtil;
import com.hdtx.base.common.exception.BaseErrorInfo;
import com.hdtx.base.common.exception.CommonEnum;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: ghx
 * @date 2021/8/13
 * @describe:
 */
@Slf4j
public class ResultBody<T> {

    /**
     * 响应代码
     */
    private int code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应结果
     */
    private T data;

    public ResultBody() {

    }

    public ResultBody(BaseErrorInfo errorInfo) {
        this.code = errorInfo.getResultCode();
        this.msg = errorInfo.getResultMsg();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 成功
     */
    public static <T> ResultBody<T> success() {
        return success(null);
    }

    /**
     * 成功
     */
    public static <T> ResultBody<T> ok(T data) {
        ResultBody<T> rb = new ResultBody();
        rb.setCode(CommonEnum.SUCCESS.getResultCode());
        rb.setMsg(CommonEnum.SUCCESS.getResultMsg());
        rb.setData(data);
        log.info(JSONUtil.toJsonStr(rb));
        return rb;
    }

    public static ResultBody success(Object data) {
        ResultBody rb = new ResultBody();
        rb.setCode(CommonEnum.SUCCESS.getResultCode());
        rb.setMsg(CommonEnum.SUCCESS.getResultMsg());
        rb.setData(data);
        log.info(JSONUtil.toJsonStr(rb));
        return rb;
    }

    /**
     * 自定义返回状态
     *
     * @param message
     * @param code    [message, code]* @return com.hdtx.common.model.ResultBody<T>
     * @author xiaoLin
     * @creed: Talk is cheap,show me the code
     * @date 2021/8/19 0019 9:30
     */
    public static <T> ResultBody<T> create(String message, int code) {
        return create(message, code, null);
    }

    public static <T> ResultBody<T> create(String message, int code, T date) {
        ResultBody<T> rb = new ResultBody();
        rb.setCode(code);
        rb.setMsg(message);
        rb.setData(date);
        return rb;
    }


    /**
     * 失败
     */
    public static ResultBody error(BaseErrorInfo errorInfo) {
        ResultBody rb = new ResultBody();
        rb.setCode(errorInfo.getResultCode());
        rb.setMsg(errorInfo.getResultMsg());
        return rb;
    }

    /**
     * 失败
     */
    public static ResultBody error(int code, String message) {
        ResultBody rb = new ResultBody();
        rb.setCode(code);
        rb.setMsg(message);
        return rb;
    }

    /**
     * 失败
     */
    public static ResultBody error(String message) {
        ResultBody rb = new ResultBody();
        rb.setCode(CommonEnum.SYSTEM_ERROR.getResultCode());
        rb.setMsg(message);
        rb.setData(null);
        return rb;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
