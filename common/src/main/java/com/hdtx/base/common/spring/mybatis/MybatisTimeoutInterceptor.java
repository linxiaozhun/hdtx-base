package com.hdtx.base.common.spring.mybatis;

import com.hdtx.base.common.spring.ds.DataSourceHolder;
import com.hdtx.base.common.spring.ds.DataSourceTimeoutResolver;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.executor.statement.StatementUtil;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

/**
 * 设置data source超时时间
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class MybatisTimeoutInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(MybatisTimeoutInterceptor.class);

    public Object intercept(Invocation invocation) throws Throwable {
        Statement statement = (Statement)invocation.proceed();
        try {
            Integer transactionTimeout = (Integer) invocation.getArgs()[1];
            if(statement != null && DataSourceTimeoutResolver.testIfNeedSetTimeout(statement.getQueryTimeout())) {
                // 如果mybatis设置了全局timeout, 或者sql单独设置了timeout, 或者设置了事务timeout
                // 就不会设置数据源timeout
                Integer dataSourceTimeout = null;
                String dataSource = DataSourceHolder.getDataSource();
                if(dataSource == null) {
                    //默认数据源
                    dataSource = DataSourceHolder.defaultDataSource4Record;
                }
                if(dataSource != null) {
                    dataSourceTimeout = DataSourceTimeoutResolver.getDataSourceTimeoutInSeconds(dataSource);
                }
                if(dataSourceTimeout != null) {
                    statement.setQueryTimeout(dataSourceTimeout);
                    StatementUtil.applyTransactionTimeout(statement, dataSourceTimeout, transactionTimeout);
                }
            }
        } catch (Exception e) {
            logger.error("MybatisTimeoutInterceptor error: " + e.getMessage(), e);
        }
        return statement;
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties0) {
        // this.properties = properties0;
    }
}
