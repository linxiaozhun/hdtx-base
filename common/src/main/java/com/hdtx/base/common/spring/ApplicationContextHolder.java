package com.hdtx.base.common.spring;

import com.hdtx.base.common.log.PerformanceLogUtil;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author liubin
 * @Date 2017/5/15 15:03
 */
public class ApplicationContextHolder {

    public volatile static ApplicationContext context;

    @Deprecated
    public volatile static ApplicationConstant constant;

    private static AtomicBoolean startup = new AtomicBoolean(false);

    public static final ApplicationContextHolder INSTANCE = new ApplicationContextHolder();

    private ApplicationContextHolder() {
    }

    public static ApplicationContextHolder getInstance() {
        return INSTANCE;
    }

    public synchronized void init(ApplicationConstant applicationConstant) {
        constant = applicationConstant;
        PerformanceLogUtil.init(constant);
    }

    public static boolean tryStartup() {
        return startup.compareAndSet(false, true);
    }

    public static boolean isStartup() {
        return startup.get();
    }

}
