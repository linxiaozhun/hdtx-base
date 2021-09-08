package com.hdtx.base.common.spring.actuator.dto;

import java.util.List;

public class ResourceAwareDTO {

    private List<DataSourceResourceDTO> dataSourceResources;

    private List<RedisResourceDTO> redisResources;

    private List<RabbitResourceDTO> rabbitResources;


    public ResourceAwareDTO(List<DataSourceResourceDTO> dataSourceResources, List<RedisResourceDTO> redisResources,
                            List<RabbitResourceDTO> rabbitResources) {
        this.dataSourceResources = dataSourceResources;
        this.redisResources = redisResources;
        this.rabbitResources = rabbitResources;
    }

    public List<DataSourceResourceDTO> getDataSourceResources() {
        return dataSourceResources;
    }

    public void setDataSourceResources(List<DataSourceResourceDTO> dataSourceResources) {
        this.dataSourceResources = dataSourceResources;
    }

    public List<RedisResourceDTO> getRedisResources() {
        return redisResources;
    }

    public void setRedisResources(List<RedisResourceDTO> redisResources) {
        this.redisResources = redisResources;
    }

    public List<RabbitResourceDTO> getRabbitResources() {
        return rabbitResources;
    }

    public void setRabbitResources(List<RabbitResourceDTO> rabbitResources) {
        this.rabbitResources = rabbitResources;
    }
}
