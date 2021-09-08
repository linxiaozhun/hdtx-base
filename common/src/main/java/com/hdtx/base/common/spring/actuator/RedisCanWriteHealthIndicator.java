package com.hdtx.base.common.spring.actuator;

import cn.hutool.core.util.ObjectUtil;
import com.hdtx.base.common.exception.AppBusinessException;
import com.hdtx.base.common.spring.ApplicationConstant;
import com.hdtx.base.common.spring.ApplicationContextHolder;
import com.hdtx.base.common.utils.DateTimeUtils;
import com.hdtx.base.utils.spring.SpringUtils;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.redis.RedisHealthIndicator;
import org.springframework.data.redis.connection.ClusterInfo;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class RedisCanWriteHealthIndicator extends RedisHealthIndicator {

    private static final String VERSION = "version";

    private static final String REDIS_VERSION = "redis_version";

    private final RedisConnectionFactory redisConnectionFactory;

    public static final String WRITE_KEY = "SPRING_HEALTH_TEST_KEY";

    private Charset charset = Charset.forName("UTF-8");

    public RedisCanWriteHealthIndicator(RedisConnectionFactory connectionFactory) {
        super(connectionFactory);
        this.redisConnectionFactory = connectionFactory;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        RedisConnection connection = RedisConnectionUtils
                .getConnection(this.redisConnectionFactory);
        try {
            if (connection instanceof RedisClusterConnection) {
                ClusterInfo clusterInfo = ((RedisClusterConnection) connection)
                        .clusterGetClusterInfo();
                builder.up().withDetail("cluster_size", clusterInfo.getClusterSize())
                        .withDetail("slots_up", clusterInfo.getSlotsOk())
                        .withDetail("slots_fail", clusterInfo.getSlotsFail());
            }
            else {
                Properties info = connection.info();
                Map.Entry<String, String> entry = writeTestValue(connection).entrySet().iterator().next();
                builder.up().withDetail(VERSION, info.getProperty(REDIS_VERSION)).withDetail(entry.getKey(), entry.getValue());
            }
        }
        finally {
            RedisConnectionUtils.releaseConnection(connection,
                    this.redisConnectionFactory);
        }
    }

    private Map<String, String> writeTestValue(RedisConnection connection) {
        ApplicationConstant constant = SpringUtils.getBean(ApplicationConstant.class);
        String applicationName;
        applicationName = constant.applicationName;
        String key = WRITE_KEY + "_"  + applicationName;
        String value = DateTimeUtils.format(LocalDateTime.now());
        connection.set(key.getBytes(charset), value.getBytes(charset));

        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
