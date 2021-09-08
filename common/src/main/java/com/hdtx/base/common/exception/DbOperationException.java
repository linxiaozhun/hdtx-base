package com.hdtx.base.common.exception;

/**
 * @program: people-defence-service
 * @description: 数据操作异常类
 * @author: xiaoLin
 * @create: 2021-08-19 09:24
 **/
public class DbOperationException extends RuntimeException {

    public DbOperationException(String message) {
        super(message);
    }

    public DbOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
