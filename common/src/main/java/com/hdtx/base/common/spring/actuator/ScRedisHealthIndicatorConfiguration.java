package com.hdtx.base.common.spring.actuator;

import org.springframework.boot.actuate.autoconfigure.health.CompositeHealthContributorConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.CompositeHealthIndicatorConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.CompositeReactiveHealthContributorConfiguration;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class ScRedisHealthIndicatorConfiguration extends CompositeHealthContributorConfiguration<RedisCanWriteHealthIndicator, RedisConnectionFactory> {

    private final Map<String, RedisConnectionFactory> redisConnectionFactories;

    public ScRedisHealthIndicatorConfiguration(Map<String, RedisConnectionFactory> redisConnectionFactories) {

        this.redisConnectionFactories = filterConnectionFactories(redisConnectionFactories);
    }

    private Map<String, RedisConnectionFactory> filterConnectionFactories(Map<String, RedisConnectionFactory> redisConnectionFactories) {
        Map<String, RedisConnectionFactory> factories = new LinkedHashMap<>();
        if(redisConnectionFactories != null) {
            for(Map.Entry<String, RedisConnectionFactory> entry : redisConnectionFactories.entrySet()) {
                RedisConnectionFactory factory = entry.getValue();
                if(factory != null) {
                    if(factory instanceof JedisConnectionFactory) {
                        //如果是localhost, 一般来说是没有配置redis, 就不用去校验
                        JedisConnectionFactory jedisFactory = (JedisConnectionFactory) factory;
                        if(jedisFactory.getHostName().equalsIgnoreCase("localhost")) {
                            continue;
                        }
                    }

                    factories.put(entry.getKey(), factory);
                }
            }
        }

        return factories;

    }

    @Bean(name = "redisHealthIndicator")
    public HealthContributor redisHealthIndicator() {
        if(redisConnectionFactories == null || redisConnectionFactories.isEmpty()) {
            return new ScEmptyHealthIndicator();
        } else {
            return  createComposite(this.redisConnectionFactories);
        }
    }

}
