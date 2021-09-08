package com.hdtx.base.common.log;

/**
 * @Author liubin
 * @Date 2017/7/24 16:56
 */
public enum PerformanceLogType {
    
    /**
     * Spring MVC 请求
     */
    SPRING_REQ,

    /**
     * Spring MVC 响应
     */
    SPRING_RESP,

    /**
     * REST TEMPLATE 请求
     */
    REST_REQ,

    /**
     * REST TEMPLATE 响应
     */
    REST_RESP,

    /**
     * FEIGN 请求
     */
    FEIGN_REQ,

    /**
     * FEIGN 响应
     */
    FEIGN_RESP,

    /**
     * OKHTTP 请求
     */
    OKHTTP_REQ,

    /**
     * OKHTTP 响应
     */
    OKHTTP_RESP,

    /**
     * SQL 请求
     */
    SQL_REQ,

    /**
     * SQL 响应
     */
    SQL_RESP,

    /**
     * MQ消息发送
     */
    MQ_SEND_REQ,

    /**
     * MQ消息接收开始
     */
    MQ_RECEIVE_REQ,

    /**
     * MQ消息接收与处理结束
     */
    MQ_RECEIVE_RESP,

    /**
     * APP端响应结果
     */
    APP_RESP,

    /**
     * 定时任务开始
     */
    JOB_REQ,

    /**
     * 定时任务结束
     */
    JOB_RESP,

    /**
     * websocket client发送消息到server
     */
    WS_CLIENT_REQ,

    /**
     * websocket client接收server消息
     */
    WS_CLIENT_RESP,

    /**
     * websocket server接收client消息
     */
    WS_SERVER_REQ,

    /**
     * websocket server发送消息到client
     */
    WS_SERVER_RESP


}
