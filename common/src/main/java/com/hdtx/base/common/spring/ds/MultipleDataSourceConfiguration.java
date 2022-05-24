package com.hdtx.base.common.spring.ds;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.hdtx.base.apiutils.api.CommonError;
import com.hdtx.base.apiutils.exception.AppBusinessException;
import com.hdtx.base.common.component.DataSourcePropertiesProcessor;
import com.hdtx.base.common.spring.ApplicationConstant;
import com.hdtx.base.common.spring.ApplicationContextHolder;
import com.hdtx.base.common.spring.mybatis.MybatisInterceptor;
import com.hdtx.base.common.spring.mybatis.MybatisMateProperties;
import com.hdtx.base.common.spring.mybatis.MybatisSQLPerformanceInterceptor;
import com.hdtx.base.common.spring.mybatis.MybatisTimeoutInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.*;

/**
 * @Author liubin
 * @Date 2017/7/14 10:00
 */
@Configuration
@EnableConfigurationProperties(MybatisMateProperties.class)
@EnableTransactionManagement
public class MultipleDataSourceConfiguration implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(MultipleDataSourceConfiguration.class);

    private ApplicationContext applicationContext;

    private static final Map<Object, Object> dataSourceMap = new HashMap<>();

    @Bean
    public DataSourceInterceptor dataSourceInterceptor() {
        return new DataSourceInterceptor();
    }


    @Bean(name = "dynamicDataSource")
    public AbstractRoutingDataSource dataSource(MultipleDataSource multipleDataSource,
                                                MultipleDataSourceInitializer initializer) {

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        //如果指定的dataSource key不存在, 则报错
        dynamicDataSource.setLenientFallback(false);

        for (String ds : multipleDataSource.getDataSources()) {

            DruidDataSource dataSource = applicationContext.getBean(ds, DruidDataSource.class);

            //设置默认数据库
            if (ds.equalsIgnoreCase(multipleDataSource.getDefaultDataSource())) {
                dynamicDataSource.setDefaultTargetDataSource(dataSource);
            }
            dataSourceMap.put(ds, dataSource);
        }

        logger.info("DataSourceMap.keySet(): {}", dataSourceMap.keySet());

        /**
         * sharding datasource bean 依赖顺序
         *
         * dynamicDataSource -> ShardingDataSourceCollector -> ShardingDataSource(ShardingDataSourceFactoryBean) ->
         * multipleDataSourceInitializer -> MultipleDataSource, DataSourcePropertiesProcessor
         *
         */
        dynamicDataSource.setTargetDataSources(dataSourceMap);

        return dynamicDataSource;
    }

    @Bean(name = "transactionManager")
    @ConditionalOnMissingBean
    public PlatformTransactionManager transactionManager(AbstractRoutingDataSource dynamicDataSource) {
        return new DataSourceTransactionManager(dynamicDataSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public MybatisInterceptorConfigurer mybatisPluginConfigurer() {
        return new MybatisInterceptorConfigurer.Default();
    }


    @Bean
    public MybatisMateHandler mybatisMateHandler(MybatisMateProperties mybatisMateProperties) {
        return new MybatisMateHandler(mybatisMateProperties);
    }

    @Bean(name = "sqlSessionFactory")
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(AbstractRoutingDataSource dynamicDataSource,
                                               MybatisInterceptorConfigurer mybatisInterceptorConfigurer,MybatisMateProperties mybatisMateProperties) throws Exception {

        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dynamicDataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setMapperLocations(resolver.getResources("classpath*:mapper/*.xml"));

        List<Interceptor> list = new ArrayList<>(mybatisInterceptorConfigurer.supplyInterceptors());
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        list.add(new MybatisInterceptor());
        list.add(new MybatisSQLPerformanceInterceptor());
        list.add(new MybatisTimeoutInterceptor());
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.getDbType(dynamicDataSource.getConnection().getMetaData().getDatabaseProductName())));
        list.add(mybatisPlusInterceptor);
        factoryBean.setPlugins(list.toArray(new Interceptor[0]));
        Properties properties = new Properties();
        properties.put("dialect", "mysql");
        factoryBean.setConfigurationProperties(properties);
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        factoryBean.setConfiguration(mybatisConfiguration);
        factoryBean.setFailFast(true);
        GlobalConfig global = GlobalConfigUtils.defaults();
        global.setMetaObjectHandler(mybatisMateHandler(mybatisMateProperties));
        factoryBean.setGlobalConfig(global);
        SqlSessionFactory sqlSessionFactory = factoryBean.getObject();
        //设置默认全局超时时间
        Integer defaultStatementTimeout = sqlSessionFactory.getConfiguration().getDefaultStatementTimeout();
        if (defaultStatementTimeout == null) {
            ApplicationConstant applicationConstant = applicationContext.getBean(ApplicationConstant.class);
            sqlSessionFactory.getConfiguration().setDefaultStatementTimeout(applicationConstant.globalDataSourceTimeout);
        }
        return sqlSessionFactory;
    }


    /**
     * 根据配置的MultipleDataSource, 创建多个数据源bean
     *
     * @param registry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        MultipleDataSource multipleDataSource = applicationContext.getBean(MultipleDataSource.class);
        if (multipleDataSource == null) {
            throw new AppBusinessException(CommonError.INTERNAL_ERROR, "multipleDataSource cannot be null, " +
                    "配置了MultipleDataSourceConfiguration就一定要定义MultipleDataSource");
        }
        Set<String> dataSources = multipleDataSource.getDataSources();
        String defaultDataSource = multipleDataSource.getDefaultDataSource();

        for (String ds : dataSources) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DruidDataSource.class);
            if (ds.equalsIgnoreCase(defaultDataSource)) {
                builder.getBeanDefinition().setPrimary(true);
            }
            registry.registerBeanDefinition(ds, builder.getBeanDefinition());
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean(name = MultipleDataSourceInitializer.BEAN_NAME)
    public MultipleDataSourceInitializer multipleDataSourceInitializer(MultipleDataSource multipleDataSource,
                                                                       DataSourcePropertiesProcessor processor) throws Exception {
        for (String ds : multipleDataSource.getDataSources()) {

            DruidDataSource dataSource = applicationContext.getBean(ds, DruidDataSource.class);

            //读取配置文件
            processor.postProcessBeforeInitialization(dataSource, ds, ds);

            initDruidDataSource(dataSource);

            //如果没有配置socket超时, 设置socket超时时间为3分钟
//            Properties connectProperties = dataSource.getConnectProperties();
//            if(connectProperties != null && !connectProperties.contains("socketTimeout")) {
//                dataSource.addConnectionProperty("socketTimeout", "180000");
//            }

        }

        return MultipleDataSourceInitializer.DEFAULT;
    }

    /**
     * 初始化druid数据源配置, 现在不需要在配置文件里配置一些默认参数了
     *
     * @param dataSource
     */
    private void initDruidDataSource(DruidDataSource dataSource) {

        try {

            //设置默认参数
            if (dataSource.getMaxActive() == 8 || dataSource.getMaxActive() == 5) {
                dataSource.setMaxActive(100);
            }

            if (dataSource.getInitialSize() == 0 || dataSource.getInitialSize() == 1) {
                dataSource.setInitialSize(10);
            }

            if (dataSource.getMinIdle() == 0) {
                dataSource.setMinIdle(10);
            }


            if (!dataSource.isPoolPreparedStatements()) {
                dataSource.setMaxPoolPreparedStatementPerConnectionSize(5);
            }

            //设置获取连接的最大等待时间为10s
            if (dataSource.getMaxWait() < 0 || dataSource.getMaxWait() > 5000L) {
                dataSource.setMaxWait(5000L);
            }

            if (dataSource.getValidationQuery() == null) {
                dataSource.setValidationQuery("SELECT 'x'");
            }

            if (dataSource.getValidationQueryTimeout() < 0) {
                dataSource.setValidationQueryTimeout(0);
            }

        } catch (Exception e) {
            logger.error("初始化druid数据源发生错误, ex: " + e.getMessage());
        }

    }
}
