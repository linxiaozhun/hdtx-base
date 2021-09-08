package com.hdtx.base.common.spring.mybatis;

import com.alibaba.druid.pool.DruidPooledStatement;
import com.hdtx.base.apiutils.utils.DateTimeUtils;
import com.hdtx.base.common.spring.ds.DataSourceHolder;
import com.hdtx.base.common.log.*;
import com.hdtx.base.common.spring.ApplicationConstant;
import com.hdtx.base.common.spring.ApplicationContextHolder;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.function.Predicate;

/**
 * 获取执行的sql信息并打印
 *
 * @author chenshaofeng 2017-11-23
 */
@Intercepts(//@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
        {@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class})
                , @Signature(type = StatementHandler.class, method = "update", args = {Statement.class})
        })
public class MybatisSQLPerformanceInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(MybatisSQLPerformanceInterceptor.class);
    private static final String MAPPED_STATEMENT = "delegate.mappedStatement";

    public Object intercept(Invocation invocation) throws Throwable {

        SQLRespLog sqlRespLog = null;
        String error = null;
        Long start = System.currentTimeMillis();
        boolean pluginHasError = false;
        boolean skip = false;
        PerformanceLogLevel logLevel = null;
        ApplicationConstant applicationConstant = null;

        try {
            applicationConstant = ApplicationContextHolder.context.getBean(ApplicationConstant.class);
            logLevel = applicationConstant.determinePerformanceLogType();
            skip = logLevel == null || logLevel.equals(PerformanceLogLevel.NONE) || !applicationConstant.dsShowLog || applicationConstant.performanceLogIgnoreSql;
        } catch (Exception e) {
            pluginHasError = true;
            logger.error("MybatisSQLPerformanceInterceptor error: " + e.getMessage(), e);
        }

        if (skip) {
            return invocation.proceed();
        }

        try {
            sqlRespLog = new SQLRespLog(PerformanceLogType.SQL_RESP, logLevel);
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = statementHandler.getBoundSql();
            String sql = PerformanceLog.trim(boundSql.getSql());
            sqlRespLog.setSql(sql);
            //数据源的key, 例如db.ds.read.common
            String dataSourceKey = DataSourceHolder.getDataSource();
            if (dataSourceKey == null) {
                //默认数据源
                dataSourceKey = DataSourceHolder.defaultDataSource4Record;
            }
            sqlRespLog.setDataSourceKey(dataSourceKey);

            MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
            MappedStatement mappedStatement = (MappedStatement) metaObject.getValue(MAPPED_STATEMENT);
            sqlRespLog.setSqlId(mappedStatement.getId());
            Object[] objs = invocation.getArgs();
            Object obj = objs[0];
            String databaseType = null;
            if (obj instanceof Connection) {//mysql
                Connection c = (Connection) obj;
                databaseType = c.getCatalog();
            } else if (obj instanceof DruidPooledStatement) {//sqlserver
                DruidPooledStatement ds = (DruidPooledStatement) obj;
                Connection c = ds.getConnection();
                databaseType = c.getCatalog();
            }
            //连接的数据库名字, 例如www_Junte_com
            sqlRespLog.setDatabaseType(databaseType);

            //暂时不打印SQL_REQ
//            SQLReqLog sqlReqLog = new SQLReqLog(PerformanceLogType.SQL_REQ, logLevel);
//            sqlReqLog.setAddDate(beginDateStr);
//            sqlReqLog.setBeginDate(beginDateStr);
//            sqlReqLog.setSql(sql);
//            sqlReqLog.setDataSourceKey(sqlRespLog.getDataSourceKey());
//            sqlReqLog.setSqlId(sqlId);
//            sqlReqLog.setDatabaseType(databaseType);
//            logger.info(sqlReqLog.toString());

        } catch (Exception e) {
            pluginHasError = true;
            logger.error("MybatisSQLPerformanceInterceptor error: " + e.getMessage(), e);
        }

        Object statement = null;
        try {
            statement = invocation.proceed();
        } catch (InvocationTargetException e) {
            Throwable te = e.getTargetException();
            error = PerformanceLogUtil.logError(te, new SQLExceptionIgnorePredicates(applicationConstant));
            throw e;
        } catch (Exception e) {
            error = PerformanceLogUtil.logError(e, new SQLExceptionIgnorePredicates(applicationConstant));
            throw e;
        } finally {
            try {
                if (!pluginHasError) {

                    long end = System.currentTimeMillis();
                    sqlRespLog.setError(error);
                    LocalDateTime endDate = LocalDateTime.now();
                    String endDateStr = DateTimeUtils.format(endDate, "yyyy-MM-dd HH:mm:ss.SSS");
                    sqlRespLog.setSqlUsedTime(end - start);
                    logger.info(sqlRespLog.toString());
                }
            } catch (Exception e) {
                logger.error("MybatisSQLPerformanceInterceptor error: " + e.getMessage(), e);
            }
        }
        return statement;
    }

    /**
     * 获取SqlId
     *
     * @param plugin
     * @return
     */
    private String getSqlId(Plugin plugin) {
        try {
            Field[] fs = Plugin.class.getDeclaredFields();
            for (Field field : fs) {
                field.setAccessible(true);
                Object oo = field.get(plugin);
                if (oo instanceof RoutingStatementHandler) {
                    RoutingStatementHandler rh = (RoutingStatementHandler) oo;
                    Field[] fs2 = RoutingStatementHandler.class.getDeclaredFields();
                    for (Field field2 : fs2) {
                        field2.setAccessible(true);

                        Object rh2 = field2.get(rh);
                        if (rh2 instanceof BaseStatementHandler) {
                            BaseStatementHandler sh = (BaseStatementHandler) rh2;

                            Field[] fs3 = BaseStatementHandler.class.getDeclaredFields();
                            for (Field field3 : fs3) {
                                field3.setAccessible(true);

                                Object rh3 = field3.get(sh);
                                if (rh3 instanceof MappedStatement) {
                                    MappedStatement ms = (MappedStatement) rh3;
                                    String id = ms.getId();
                                    return id;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("MybatisSQLPerformanceInterceptor 获取 sqlId发生错误", e);
        }

        return null;
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties0) {
        // this.properties = properties0;
    }

    static class SQLExceptionIgnorePredicates implements Predicate<Throwable> {

        private ApplicationConstant applicationConstant;

        public SQLExceptionIgnorePredicates(ApplicationConstant applicationConstant) {
            this.applicationConstant = applicationConstant;
        }

        @Override
        public boolean test(Throwable e) {

            if (applicationConstant != null && applicationConstant.performanceLogIgnoreSqlDuplicateConstraint) {
                //判断如果是MySQLIntegrityConstraintViolationException并且包含Duplicate entry, 则忽略异常
                if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                    return true;
                }
            }

            return false;
        }
    }


}
