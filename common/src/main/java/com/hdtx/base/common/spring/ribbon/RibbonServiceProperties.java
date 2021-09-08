package com.hdtx.base.common.spring.ribbon;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Ribbon URL 超时配置项
 */
@ConfigurationProperties("app.feign")
public class RibbonServiceProperties {

    private Boolean enabled = true;

    private Integer period = 10000;

    private List<ServiceProperties> services = new ArrayList<>();

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public List<ServiceProperties> getServices() {
        return services;
    }

    public void setServices(List<ServiceProperties> services) {
        this.services = services;
    }

    public static class ServiceProperties {

        private String serviceName;
        private List<UrlProperties> urls = new ArrayList<>();

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public List<UrlProperties> getUrls() {
            return urls;
        }

        public void setUrls(List<UrlProperties> urls) {
            this.urls = urls;
        }

        @Override
        public String toString() {
            return "ServiceProperties{" +
                    "serviceName='" + serviceName + '\'' +
                    ", urls=" + urls +
                    '}';
        }
    }

    public static class UrlProperties {

        private String url;
        private String method;
        private Integer connectTimeout;
        private Integer readTimeout;

        public UrlProperties() {
        }

        public UrlProperties(String url, String method, Integer connectTimeout, Integer readTimeout) {
            this.url = url;
            this.method = method;
            this.connectTimeout = connectTimeout;
            this.readTimeout = readTimeout;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public Integer getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public Integer getReadTimeout() {
            return readTimeout;
        }

        public void setReadTimeout(Integer readTimeout) {
            this.readTimeout = readTimeout;
        }

        @Override
        public String toString() {
            return "UrlProperties{" +
                    "url='" + url + '\'' +
                    ", method='" + method + '\'' +
                    ", connectTimeout=" + connectTimeout +
                    ", readTimeout=" + readTimeout +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "RibbonServiceProperties{" +
                "services=" + services +
                '}';
    }
}
