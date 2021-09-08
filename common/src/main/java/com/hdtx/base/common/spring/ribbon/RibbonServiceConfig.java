package com.hdtx.base.common.spring.ribbon;

public class RibbonServiceConfig {

    private Integer connectTimeout;

    private Integer readTimeout;

    public RibbonServiceConfig(Integer connectTimeout, Integer readTimeout) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }
}
