package com.hdtx.base.common.log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hdtx.base.common.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author liubin
 * @Date 2017/7/19 14:42
 */
public abstract class PerformanceLog implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceLog.class);

    public static final int SIMPLE_MAX_SIZE = 1000;

    public static final String LOG_PREFIX = "PerfLog";

    /**
     * 日志类型
     */
    protected PerformanceLogType type;

    //暂时不生成uuid
    @JsonIgnore
    protected String uuid;

    @JsonIgnore
    protected PerformanceLogLevel performanceLogLevel;

    public PerformanceLog() {}

    public PerformanceLog(PerformanceLogType type, PerformanceLogLevel performanceLogLevel) {
        this.type = type;
        this.performanceLogLevel = performanceLogLevel;
    }

    public PerformanceLogType getType() {
        return type;
    }

    public void setType(PerformanceLogType type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public PerformanceLogLevel getPerformanceLogLevel() {
        return performanceLogLevel;
    }

    public void setPerformanceLogLevel(PerformanceLogLevel performanceLogLevel) {
        this.performanceLogLevel = performanceLogLevel;
    }

    @Override
    public String toString() {
        return String.format("%s_%s:%s", LOG_PREFIX, typeToToken(type), JsonUtils.object2Json(this));
    }

    private String typeToToken(PerformanceLogType type) {
        String token = "0";
        if(type == null) {
            logger.error("PerformanceLogType is null");
        } else if(type.name().endsWith("REQ")){
            token = "0";
        } else if(type.name().endsWith("RESP")){
            token = "1";
        }
        return token;
    }

    public static String tryTrimToSimple(String str, PerformanceLogLevel performanceLogLevel) {

        if(performanceLogLevel.equals(PerformanceLogLevel.SIMPLE)) {
            return trim(str);
        }

        return str;

    }

    public static String trim(String str) {

        if(str != null && str.length() > SIMPLE_MAX_SIZE) {
            return str.substring(0, SIMPLE_MAX_SIZE);
        }

        return str;
    }


    protected static void swallowException(RunnableThrowable runnable) {

        try {
            runnable.run();
        } catch (Exception e) {
            logger.error("生成PerformanceLog的时候发生错误", e);
        }

    }

    protected static String trimRequestUri(String uri) {
        if(uri == null) return null;
        return uri.startsWith("//") ? uri.replaceFirst("//", "/") : uri;
    }

    protected static Map<String, List<String>> removeIgnoreHeader(Map<String, List<String>> map) {
        map.entrySet().removeIf(entry -> PerformanceLogUtil.isIgnoreHeader(entry.getKey()));
        return map;
    }


    @FunctionalInterface
    interface RunnableThrowable {
        void run() throws Exception;
    }

}
