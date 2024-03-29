package com.hdtx.base.common.spring;

import com.hdtx.base.common.log.PerformanceLogLevel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Author liubin
 * @Date 2017/5/15 15:03
 */
public class ApplicationConstant {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConstant.class);

    /**
     * zookeeper地址
     */
    @Value("${app.zookeeper.address:}")
    public String zookeeperAddress;

    /**
     * 执行任务的线程数量
     */
    @Value("${app.scheduler.thread.count:10}")
    public int schedulerThreadCount;

    @Value("${spring.application.name}")
    public String applicationName;

    /**
     * 是否是web站点(对外提供服务), 默认false.
     * 如果设置为true, swagger文档默认不显示. 如果项目有页面, 需要在templates目录下提供404和401的thymeleaf页面
     */
    @Value("${app.web.project:false}")
    public boolean webProject;

    /**
     * NONE 不打印日志
     * MINIMUM 不打印请求消息体, 请求参数, 请求响应头
     * SIMPLE 打印精简的日志
     * ALL 打印所有日志
     */
    @Value("${app.performance.log:NOTSET}")
    public String performanceLogType;

    @Value("${app.performance.log.ignore.urls:}")
    public String[] performanceLogIgnoreUrls;

    @Value("${app.performance.log.ignore.urlParams:}")
    public String[] performanceLogIgnoreUrlParams;

    /**
     * 需要忽略的http头
     */
    @Value("${app.performance.log.ignore.headers:}")
    public String[] performanceLogIgnoreHeaders;

    @Value("${app.performance.log.ignore.sql:false}")
    public boolean performanceLogIgnoreSql;

    @Value("${app.performance.log.ignore.mq:false}")
    public boolean performanceLogIgnoreMq;

    @Value("${app.performance.log.ignore.job:false}")
    public boolean performanceLogIgnoreJob;

    @Value("${app.performance.log.ignore.sql.duplicateConstraint:false}")
    public boolean performanceLogIgnoreSqlDuplicateConstraint;

    @Value("${spring.cloud.config.profile:dev}")
    public String profile;

    @Value("${app.okhttp.read.timeout:10000}")
    public long okHttpReadTimeout;

    @Value("${app.okhttp.connect.timeout:5000}")
    public long okHttpConnectTimeout;

    @Value("${app.okhttp.write.timeout:10000}")
    public long okHttpWriteTimeout;

    /**
     * 最大空闲连接数
     */
    @Value("${app.okhttp.max.idle:5}")
    public int okHttpMaxIdle;

    /**
     * 连接存活时间, 单位: 秒
     */
    @Value("${app.okhttp.alive.duration:300}")
    public int okHttpAliveDuration;

    /**
     * 是否打印sharding jdbc日志
     */
    @Value("${app.sjdbc.show.log:true}")
    public boolean sjdbcShowLog;

    /**
     * sharding jdbc执行的线程池数量, 默认为可用CPU核心数
     */
    @Value("${app.sjdbc.executor.size:0}")
    public int sjdbcExecutorSize;

    /**
     * 是否打印sql日志
     */
    @Value("${app.ds.show.log:true}")
    public boolean dsShowLog;

    /**
     * 全局sql超时时间, 默认60秒
     */
    @Value("${app.ds.timeout.global:60}")
    public int globalDataSourceTimeout;


    /**
     * 新的zookeeper地址
     */
    @Value("${config.zk.address:}")
    private String newZookeeperAddress;

    /**
     * 确认用哪种性能日志级别
     * @return
     */
    public PerformanceLogLevel determinePerformanceLogType() {
        PerformanceLogLevel type = PerformanceLogLevel.NOTSET;
        try {
            type = PerformanceLogLevel.valueOf(performanceLogType.toUpperCase());
        } catch (Exception e) {
            logger.error("", e);
        }
        if(type.equals(PerformanceLogLevel.NOTSET)) {
            if(isProdProfile()) {
                return PerformanceLogLevel.SIMPLE;
            } else {
                return PerformanceLogLevel.ALL;
            }
        } else {
            return type;
        }
    }

    public boolean isDevProfile() {
        return StringUtils.isBlank(profile) || "DEV".equalsIgnoreCase(profile);
    }

    public boolean isTestProfile() {
        return "TEST".equalsIgnoreCase(profile);
    }

    public boolean isPrevProfile() {
        return "PREV".equalsIgnoreCase(profile);
    }

    public boolean isProdProfile() {
        return "PROD".equalsIgnoreCase(profile);
    }


    /**
     * 获取zk地址, 优先获取老配置的zk地址
     * @return
     */
    public String getZkAddress() {
        if(StringUtils.isNotBlank(zookeeperAddress)) {
            return zookeeperAddress;
        } else {
            return newZookeeperAddress;
        }
    }



}
