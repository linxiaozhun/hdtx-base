package com.hdtx.base.common.spring.refresh;

import com.alibaba.druid.pool.DruidConnectionHolder;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

public class DbRefreshService implements ApplicationContextAware, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(DbRefreshService.class);

    private ApplicationContext applicationContext;
    private Environment environment;

    private AtomicBoolean running = new AtomicBoolean(false);



    public synchronized List<String> refreshDb(DbRefreshCommand dbRefreshCommand) {

        List<String> refreshSuccessDbKeyList = new ArrayList<>();

        if(running.compareAndSet(false, true)) {

            try {

                Map<String, DataSource> dataSourceMap = applicationContext.getBeansOfType(DataSource.class);
                Map<String, DruidDataSource> targetMap = dataSourceMap.entrySet().stream()
                        .filter(entry -> (entry.getValue() instanceof DruidDataSource))
                        .map(entry -> new AbstractMap.SimpleEntry<>(
                                entry.getKey(),
                                DbRefreshCommand.DbInfo.fromJdbcUrl(environment.getProperty(entry.getKey() + ".url"))))
                        .filter(entry -> !entry.getValue().isNull() &&
                                (dbRefreshCommand.isRefreshAll() || dbRefreshCommand.getDbInfos().contains(entry.getValue())))
                        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, entry -> (DruidDataSource)dataSourceMap.get(entry.getKey())));

                logger.info("ready to refresh db: " + targetMap.keySet());

                targetMap.forEach((key, ds) -> {
                    if(refreshDruidDataSource(key, ds)) {
                        refreshSuccessDbKeyList.add(key);
                    }
                });

            } finally {
                running.set(false);
            }

        } else {

            logger.warn("refreshDb is processing");
        }

        return refreshSuccessDbKeyList;

    }

    private boolean refreshDruidDataSource(String key, DruidDataSource druidDataSource) {

        int count = 0;

        try {
            Field connectionsField = FieldUtils.getField(DruidDataSource.class, "connections", true);
            if (connectionsField == null) {
                logger.error("Can not get connections field in DruidDataSource");
                return false;
            }
            Lock lock = druidDataSource.getLock();
            boolean locked = false;

            try {
                locked = lock.tryLock(3, TimeUnit.SECONDS);
                if(locked) {
                    DruidConnectionHolder[] connections = (DruidConnectionHolder[]) ReflectionUtils.getField(connectionsField, druidDataSource);
                    if (connections != null && connections.length > 0) {
                        for(DruidConnectionHolder holder : connections) {
                            if(holder != null) {
                                holder.setDiscard(true);
                                count ++;
                            }
                        }
                        logger.info("Refresh DB end, data source key: {}, connections length: {}, discard count: {}", key, connections.length, count);
                    }
                }
            } finally {
                if(locked) {
                    lock.unlock();
                }
            }


        } catch (Exception e) {
            logger.error("refresh DruidDataSource failed, data source key: " + key, e);
        }

        return count > 0;

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
