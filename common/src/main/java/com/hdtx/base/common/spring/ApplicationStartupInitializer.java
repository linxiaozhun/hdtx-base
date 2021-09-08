package com.hdtx.base.common.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

/**
 * Created by chenjx on 2018/2/8.
 */
public class ApplicationStartupInitializer implements SpringApplicationRunListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupInitializer.class);

    private SpringApplication application;

    private String[] args;

    public ApplicationStartupInitializer(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
    }

    public static boolean isStartup() {
        return ApplicationContextHolder.isStartup();
    }

    @Override
    public void starting() {

    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {

    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {

    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {

    }

    @Override
    public void started(ConfigurableApplicationContext context) {
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        logger.info("onApplicationEvent执行失败.isFailed:{}" , ApplicationContextHolder.isStartup());
    }
    @Override
    public void running(ConfigurableApplicationContext context) {

        if(context.getParent() == null) return;

        boolean doStartup = false;
        Map<String, TdInitializer> tdInitializerMap = null;
        try {
            if(ApplicationContextHolder.tryStartup()) {
                doStartup =  true;
                logger.info("onApplicationEvent执行开始");
                tdInitializerMap = ApplicationContextHolder.context.getBeansOfType(TdInitializer.class, true, false);
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        if(doStartup && tdInitializerMap != null && !tdInitializerMap.isEmpty()) {
            tdInitializerMap.values().forEach(TdInitializer::init);
        }
    }
}
