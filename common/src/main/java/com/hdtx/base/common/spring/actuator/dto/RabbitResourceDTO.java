package com.hdtx.base.common.spring.actuator.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RabbitResourceDTO {

    private static final Logger logger = LoggerFactory.getLogger(RabbitResourceDTO.class);

    private List<String> urls;

    private String vhost;

    private String username;

    //git配置中配置的前缀key, 暂无数据
    private String rabbitKey;

    private String beanId;



    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getVhost() {
        return vhost;
    }

    public void setVhost(String vhost) {
        this.vhost = vhost;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRabbitKey() {
        return rabbitKey;
    }

    public void setRabbitKey(String rabbitKey) {
        this.rabbitKey = rabbitKey;
    }
}
