package com.hdtx.base.common.spring.ribbon;

import org.springframework.util.AntPathMatcher;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RibbonTimeoutMatcher {

    private RibbonServicePropertiesKeeper ribbonServicePropertiesKeeper;

    private EurekaInstanceToServiceFinder eurekaInstanceToServiceFinder;


    private AntPathMatcher pathMatcher = new AntPathMatcher();
    {
        pathMatcher.setCaseSensitive(false);
    }


    public RibbonTimeoutMatcher(RibbonServicePropertiesKeeper ribbonServicePropertiesKeeper, EurekaInstanceToServiceFinder eurekaInstanceToServiceFinder) {
        this.ribbonServicePropertiesKeeper = ribbonServicePropertiesKeeper;
        this.eurekaInstanceToServiceFinder = eurekaInstanceToServiceFinder;
    }


    public RibbonServiceConfig findRibbonServiceConfig(String url, String method) {

        Map<String, List<RibbonServiceProperties.UrlProperties>> serviceConfigMap = ribbonServicePropertiesKeeper.getRibbonServiceMap().get();
        //没有配置过ribbon url超时, 直接返回
        if(serviceConfigMap.isEmpty()) return null;

        URI uri = URI.create(url);
        //根据ip和端口查找服务名
        String serviceName = eurekaInstanceToServiceFinder.findServiceNameByInstance(uri.getHost(), uri.getPort());
        if(serviceName == null) return null;
        //根据服务名获取配置
        List<RibbonServiceProperties.UrlProperties> urlPropertiesList = serviceConfigMap.get(serviceName);
        if(urlPropertiesList == null || urlPropertiesList.isEmpty()) return null;

        //根据请求url尝试匹配配置项
        Optional<RibbonServiceProperties.UrlProperties> urlProperties = urlPropertiesList.stream()
                .filter(x -> (x.getMethod() == null || x.getMethod().equalsIgnoreCase(method))
                        && pathMatcher.match(x.getUrl(), uri.getPath())
                ).findFirst();

        return urlProperties.map(x -> new RibbonServiceConfig(x.getConnectTimeout(), x.getReadTimeout())).orElse(null);

    }

    public boolean isEnabled() {
        return ribbonServicePropertiesKeeper.isEnabled();
    }

}
