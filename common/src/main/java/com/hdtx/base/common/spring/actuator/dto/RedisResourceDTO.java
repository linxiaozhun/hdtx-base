package com.hdtx.base.common.spring.actuator.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class RedisResourceDTO {

    private static final Logger logger = LoggerFactory.getLogger(RedisResourceDTO.class);


    private List<String> sentinels;

    private String master;

    private int port;

    private String host;

    private int dbIndex;

    //git配置中配置的前缀key, 暂无数据
    private String redisKey;

    private String beanId;

    public static RedisResourceDTO fromJedisConnectionFactory(String beanId, JedisConnectionFactory jedisConnectionFactory) {

        try {
            RedisResourceDTO dto = new RedisResourceDTO();
            dto.setBeanId(beanId);
            dto.setHost(jedisConnectionFactory.getHostName());
            dto.setPort(jedisConnectionFactory.getPort());
            dto.setDbIndex(jedisConnectionFactory.getDatabase());

            Field field =
                    JedisConnectionFactory.class.getDeclaredField("sentinelConfig");
            field.setAccessible(true);
            RedisSentinelConfiguration sentinelConfiguration = (RedisSentinelConfiguration)field.get(jedisConnectionFactory);

            if(sentinelConfiguration != null) {
                dto.setMaster(sentinelConfiguration.getMaster() != null ? sentinelConfiguration.getMaster().getName() : null);
                if(sentinelConfiguration.getSentinels() != null) {
                    dto.setSentinels(sentinelConfiguration.getSentinels().stream().map(RedisNode::asString).collect(Collectors.toList()));
                }
            }

            return dto;

        } catch (NoClassDefFoundError ignore) {

            return null;
        } catch (Throwable e) {

            logger.error("", e);
            return null;
        }
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public List<String> getSentinels() {
        return sentinels;
    }

    public void setSentinels(List<String> sentinels) {
        this.sentinels = sentinels;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getDbIndex() {
        return dbIndex;
    }

    public void setDbIndex(int dbIndex) {
        this.dbIndex = dbIndex;
    }

    public String getRedisKey() {
        return redisKey;
    }

    public void setRedisKey(String redisKey) {
        this.redisKey = redisKey;
    }



}
