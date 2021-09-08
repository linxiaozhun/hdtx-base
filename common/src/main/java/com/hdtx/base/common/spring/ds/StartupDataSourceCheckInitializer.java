package com.hdtx.base.common.spring.ds;

import com.alibaba.druid.pool.DruidDataSource;
import com.hdtx.base.apiutils.exception.BaseException;
import com.hdtx.base.common.spring.TdInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Set;

public class StartupDataSourceCheckInitializer extends TdInitializer implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(StartupDataSourceCheckInitializer.class);


    private ApplicationContext applicationContext;

    @Override
    protected void doInit() {

        String maybeErrorDataSourceKey = null;

        try {
            MultipleDataSource multipleDataSource = applicationContext.getBean(MultipleDataSource.class);
            Set<String> startupNeededDataSources = multipleDataSource.getStartupNeededDataSources();
            if(startupNeededDataSources == null || startupNeededDataSources.isEmpty()) return;
            for(String dataSourceKey : startupNeededDataSources) {
                maybeErrorDataSourceKey = dataSourceKey;
                DruidDataSource dataSource = applicationContext.getBean(dataSourceKey, DruidDataSource.class);
                dataSource.getConnection();
            }
        } catch (Exception e) {
            String msg = "启动时校验数据库连接失败, 应用启动失败, dataSourceKey: " + maybeErrorDataSourceKey;
            logger.error(msg, e);
            throw new BaseException(msg);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
