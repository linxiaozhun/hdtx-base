package com.hdtx.base.common.log;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQL日志
 */
public final class SQLRespLog extends PerformanceLog {

    private static final Logger logger = LoggerFactory.getLogger(SQLRespLog.class);

    //连接的数据库名字, 例如www_Junte_com
    private String databaseType;

    //数据源的key, 例如db.ds.read.common
    private String dataSourceKey;

    /**
     * mapper文件在sqlId
     */
    private String sqlId;

    /**
     * 响应时间(ms)
     */
    private long sqlUsedTime;

    //异常信息，只有sql执行报错时才有值
    private String error;

    //sql语句
    private String sql;


    public SQLRespLog() {}

    public SQLRespLog(PerformanceLogType type, PerformanceLogLevel performanceLogLevel) {
        super(type, performanceLogLevel);
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getDataSourceKey() {
        return dataSourceKey;
    }

    public void setDataSourceKey(String dataSourceKey) {
        this.dataSourceKey = dataSourceKey;
    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    public long getSqlUsedTime() {
        return sqlUsedTime;
    }

    public void setSqlUsedTime(long sqlUsedTime) {
        this.sqlUsedTime = sqlUsedTime;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
