package com.hdtx.base.common.log;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQL日志
 */
public final class SQLReqLog extends PerformanceLog {

    private static final Logger logger = LoggerFactory.getLogger(SQLReqLog.class);

    // 创建时间, 例如2016/9/30 15:54:15.123
    private String addDate;

    //固定为Sql
    private String logType = "Sql";

    //连接的数据库名字, 例如www_Junte_com
    private String databaseType;

    //数据源的key, 例如db.ds.read.common
    private String dataSourceKey;

    //sql语句
    private String sql;

    //开始时间
    private String beginDate;

    /**
     * mapper文件在sqlId
     */
    private String sqlId;

    public SQLReqLog() {}

    public SQLReqLog(PerformanceLogType type, PerformanceLogLevel performanceLogLevel) {
        super(type, performanceLogLevel);
    }


    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
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

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }
}
