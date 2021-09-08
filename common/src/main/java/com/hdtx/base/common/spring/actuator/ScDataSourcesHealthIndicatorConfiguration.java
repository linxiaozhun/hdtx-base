package com.hdtx.base.common.spring.actuator;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.health.CompositeHealthIndicatorConfiguration;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScDataSourcesHealthIndicatorConfiguration extends CompositeHealthIndicatorConfiguration<DataSourceHealthIndicator, DataSource>{

    private final Map<String, DataSource> dataSources;

    public ScDataSourcesHealthIndicatorConfiguration(ObjectProvider<Map<String, DataSource>> dataSources) {
        this.dataSources = filterDataSources(dataSources.getIfAvailable());
    }

    private Map<String, DataSource> filterDataSources(Map<String, DataSource> candidates) {
        if (candidates == null) {
            return null;
        }
        Map<String, DataSource> dataSources = new LinkedHashMap<>();
        for (Map.Entry<String, DataSource> entry : candidates.entrySet()) {
            DataSource dataSource = entry.getValue();
            if(dataSource instanceof DruidDataSource) {
                DruidDataSource druidDataSource = (DruidDataSource) dataSource;
                String jdbcUrl = druidDataSource.getRawJdbcUrl();
                String username = druidDataSource.getUsername();
                String password = druidDataSource.getPassword();
                if(jdbcUrl != null && !jdbcUrl.trim().equals("") &&
                        username != null && !username.trim().equals("") &&
                        password != null && !password.trim().equals("")) {
                    dataSources.put(entry.getKey(), dataSource);
                }
            } else if (!(dataSource instanceof AbstractRoutingDataSource)) {

                dataSources.put(entry.getKey(), dataSource);
            }
        }
        return dataSources;

    }

    @Bean(name = "dbHealthIndicator")
    public HealthIndicator dbHealthIndicator() {
        if(dataSources == null || dataSources.isEmpty()) {
            return new ScEmptyHealthIndicator();
        } else {
            return createHealthIndicator(this.dataSources);
        }
    }


}
