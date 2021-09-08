package com.hdtx.base.common.spring.actuator;

import com.alibaba.druid.pool.DruidDataSource;
import com.hdtx.base.common.spring.actuator.dto.DataSourceResourceDTO;
import com.hdtx.base.common.spring.actuator.dto.RabbitResourceDTO;
import com.hdtx.base.common.spring.actuator.dto.RedisResourceDTO;
import com.hdtx.base.common.spring.actuator.dto.ResourceAwareDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ResourceAwareService implements ApplicationContextAware, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(ResourceAwareService.class);

    private ApplicationContext applicationContext;
    private Environment environment;

    private AtomicBoolean running = new AtomicBoolean(false);

    /**
     * 查找服务依赖的资源, 包括db, redis, rabbitmq
     * @return
     */
    public synchronized ResourceAwareDTO findResources() {

        ResourceAwareDTO resourceAwareDTO = null;

        if(running.compareAndSet(false, true)) {

            try {

                List<DataSourceResourceDTO> dataSourceResources = findDataSourceResources();

                List<RedisResourceDTO> redisResources = findRedisResources();

                List<RabbitResourceDTO> rabbitResources = new ArrayList<>();

                resourceAwareDTO = new ResourceAwareDTO(dataSourceResources, redisResources, rabbitResources);

            } finally {
                running.set(false);
            }

        } else {

            logger.warn("findResources is processing");
        }

        return resourceAwareDTO;

    }

    private List<DataSourceResourceDTO> findDataSourceResources() {

        List<DataSourceResourceDTO> dataSourceResources = new ArrayList<>();

        try {

            Map<String, DruidDataSource> dataSourceMap = applicationContext.getBeansOfType(DruidDataSource.class);

            dataSourceResources = dataSourceMap.entrySet().stream()
                    .map(entry -> DataSourceResourceDTO.fromDruidDataSource(entry.getKey(), entry.getValue()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (NoClassDefFoundError ignore) {

            return null;
        } catch (Throwable e) {

            logger.error("", e);
            return null;
        }

        return dataSourceResources;
    }

    private List<RedisResourceDTO> findRedisResources() {

        List<RedisResourceDTO> redisResources = new ArrayList<>();

        try {

            Map<String, JedisConnectionFactory> redisConnectionFactoryMap = applicationContext.getBeansOfType(JedisConnectionFactory.class);

            redisResources = redisConnectionFactoryMap.entrySet().stream()
                    .map(entry -> RedisResourceDTO.fromJedisConnectionFactory(entry.getKey(), entry.getValue()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (NoClassDefFoundError ignore) {

            return null;
        } catch (Throwable e) {

            logger.error("", e);
            return null;
        }

        return redisResources;
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
