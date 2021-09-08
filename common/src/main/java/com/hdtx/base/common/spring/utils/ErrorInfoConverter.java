package com.hdtx.base.common.spring.utils;

import com.hdtx.base.common.api.ErrorInfo;

import java.util.Map;

/**
 * 当出现异常情况时, 返回给客户端错误结果.
 * 默认使用DefaultErrorInfoConverter将ErrorInfo返回给客户端
 * 可以自定义ErrorInfoConverter bean来返回自定义的错误结果
 *
 * @Author liubin
 * @Date 2017/8/19 15:05
 */
public interface ErrorInfoConverter {

    /**
     * 返回的Map不能为null
     * @param errorInfo
     * @return
     */
    Map<String, Object> convertErrorInfoToMap(ErrorInfo errorInfo);


    /**
     * 是否根据errorInfo的httpStatus修改返回的http状态码, 默认为true
     * @return
     */
    default boolean modifyHttpStatus() {
        return true;
    }

}
