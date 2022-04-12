package com.hdtx.base.common.spring.db.config;

import com.hdtx.base.common.spring.db.annotation.DynamicReadWriteDataSource;
import com.hdtx.base.common.spring.db.interceptor.DynamicDataSourceInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author XiaoLin
 * @date 2021年12月10日
 * @time 14:38
 */
@Configuration
@ConditionalOnProperty(prefix = "multiDatasource", name = "enabled", havingValue = "true")
public class DynamicReadWriteDataSourceConfig {

    @Bean(name = "dynamicDataSourceV")
    public DynamicReadWriteDataSource dynamicReadWriteDataSource() {
        DynamicReadWriteDataSource dynamicReadWriteDataSource = new DynamicReadWriteDataSource();
        Map<Object, Object> map = new HashMap<>();
        String hdtxKey = "com.hdtx.datasource";
        map.put(hdtxKey, DynamicReadWriteDataSource.buildDataSource(hdtxKey));
        String zkdnKey = "com.zkdn.datasource";
        map.put(zkdnKey, DynamicReadWriteDataSource.buildDataSource(zkdnKey));
        dynamicReadWriteDataSource.setDefaultTargetDataSource(map.get(zkdnKey));
        dynamicReadWriteDataSource.setTargetDataSources(map);
        return dynamicReadWriteDataSource;
    }

    @Bean
    public DynamicDataSourceInterceptor dynamicDataSourceInterceptor() {
        return new DynamicDataSourceInterceptor();
    }


    @Bean
    public TransactionManager transactionManager(@Qualifier("dynamicDataSourceV") DynamicReadWriteDataSource dynamicReadWriteDataSource) {
        return new DataSourceTransactionManager(dynamicReadWriteDataSource);
    }


}
