package com.hdtx.base.common.spring.ds;

import com.hdtx.base.common.spring.ApplicationConstant;
import com.hdtx.base.common.spring.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class DataSourceTimeoutResolver  {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceTimeoutResolver.class);

    public static final String CONFIG_PREFIX = "app.ds.timeout.";

    public static final Map<String, Integer> DS_TIMEOUT_MAP = new HashMap<>();

    private static AtomicReference<Integer> GLOBAL_DEFAULT_TIMEOUT = new AtomicReference<>();

    private static Integer PLACEHOLDER = -1;

    static {
        DS_TIMEOUT_MAP.put("db.ds.read.common", 12);
        DS_TIMEOUT_MAP.put("db.ds.read.real", 12);
        DS_TIMEOUT_MAP.put("db.ds.read.low", 60);
//        DS_TIMEOUT_MAP.put("db.ds.write.test", 6);
    }

    private static volatile Map<String, Integer> dsTimeoutMap = Collections.unmodifiableMap(new HashMap<>());

    /**
     * 如果没有设置全局超时时间, 或者currentQueryTimeout等于全局超时时间(或者currentQueryTimeout没有设置),
     * 就允许设置sql的超时时间为dataSource超时时间
     * @param currentQueryTimeout
     * @return
     */
    public static boolean testIfNeedSetTimeout(int currentQueryTimeout) {

        if(currentQueryTimeout <= 0) return true;

        Integer defaultTimeout = GLOBAL_DEFAULT_TIMEOUT.get();
        if(defaultTimeout == null && ApplicationContextHolder.context != null) {
            ApplicationConstant applicationConstant = ApplicationContextHolder.context.getBean(ApplicationConstant.class);
            GLOBAL_DEFAULT_TIMEOUT.compareAndSet(null, applicationConstant.globalDataSourceTimeout);
            defaultTimeout = GLOBAL_DEFAULT_TIMEOUT.get();
        }

        return defaultTimeout != null && defaultTimeout.equals(currentQueryTimeout);

    }


    public static Integer getDataSourceTimeoutInSeconds(String dataSource) {

        Integer timeout = dsTimeoutMap.get(dataSource);
        if(timeout == null) {
            //先从配置文件获取, 如果没有设置再从默认的map里获取.
            Environment env = ApplicationContextHolder.context.getBean(Environment.class);
            timeout = env.getProperty(CONFIG_PREFIX + dataSource, Integer.class);

            if(timeout == null || timeout < 0) {
                timeout = DS_TIMEOUT_MAP.get(dataSource);
            }

            //查询完成, 替换map
            synchronized (DataSourceTimeoutResolver.class) {
                Map<String, Integer> map = new HashMap<>();
                map.putAll(dsTimeoutMap);
                map.put(dataSource, timeout == null ? PLACEHOLDER : timeout);
                dsTimeoutMap = Collections.unmodifiableMap(map);
            }
        }

        if(timeout != null && timeout.equals(PLACEHOLDER)) {
            timeout = null;
        }

        if(timeout != null && logger.isDebugEnabled()) {
            logger.debug("configure data source timeout, key: {}, timeout: {}s", CONFIG_PREFIX + dataSource, timeout);
        }

        return timeout;
    }


}
