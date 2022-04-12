package com.hdtx.base.common.spring.db.annotation;

import com.hdtx.base.common.spring.ApplicationContextHolder;
import com.hdtx.base.common.spring.db.constant.DynamicDataSourceThreadLocal;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Objects;

/**
 * @author XiaoLin
 * @date 2021年12月10日
 * @time 10:55
 */
@Slf4j
public class DynamicReadWriteDataSource extends AbstractRoutingDataSource {


    @Override
    protected Object determineCurrentLookupKey() {
        Object object = DynamicDataSourceThreadLocal.getDataSourceKey();
        return object;
    }


    public static HikariDataSource buildDataSource(String key) {
        Environment environment = ApplicationContextHolder.context.getBean(Environment.class);
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(Objects.requireNonNull(environment.getProperty(key + ".url")));
        hikariConfig.setDriverClassName(environment.getProperty(key + ".driver-class-name"));
        hikariConfig.setUsername(environment.getProperty(key + ".username"));
        hikariConfig.setPassword(environment.getProperty(key + ".password"));
        return new HikariDataSource(hikariConfig);
    }


}
