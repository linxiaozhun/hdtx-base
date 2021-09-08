package com.hdtx.base.common.spring;

import com.netflix.discovery.EurekaClient;
import com.hdtx.base.common.spring.feign.FeignCustomConfiguration;
import com.hdtx.base.common.spring.ribbon.*;
import feign.Client;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.context.annotation.Bean;

/**
 * @Author liubin
 * @Date 2017/5/15 15:04
 */
@EnableDiscoveryClient
@EnableFeignClients(value = {"com.hdtx.**.service", "com.hdtx.**.client*"},
    defaultConfiguration = FeignCustomConfiguration.class)
public class ServiceClientConfiguration {

    @Bean
    public Client feignClient(CachingSpringLoadBalancerFactory cachingFactory, SpringClientFactory clientFactory,
                              okhttp3.OkHttpClient okHttpClient, RibbonTimeoutMatcher ribbonTimeoutMatcher) {
        TdFeignOkHttpClient feignOkHttpClient = new TdFeignOkHttpClient(okHttpClient, ribbonTimeoutMatcher);
        return new LoadBalancerFeignClient(feignOkHttpClient, cachingFactory, clientFactory);
    }

    @Bean
    @RefreshScope
    public RibbonServiceProperties ribbonServiceProperties() {
        return new RibbonServiceProperties();
    }

    @Bean
    public RibbonServicePropertiesKeeper ribbonServicePropertiesKeeper(RibbonServiceProperties ribbonServiceProperties) {
        return new RibbonServicePropertiesKeeper(ribbonServiceProperties);
    }

    @Bean
    public RibbonTimeoutMatcher ribbonTimeoutMatcher(RibbonServicePropertiesKeeper ribbonServicePropertiesKeeper,
                                                     EurekaInstanceToServiceFinder eurekaInstanceToServiceFinder) {
        return new RibbonTimeoutMatcher(ribbonServicePropertiesKeeper, eurekaInstanceToServiceFinder);
    }

    @Bean
    public EurekaInstanceToServiceFinder eurekaInstanceToServiceFinder(RibbonServicePropertiesKeeper ribbonServicePropertiesKeeper,
                                                                       EurekaClient eurekaClient) {
        return new EurekaInstanceToServiceFinder(ribbonServicePropertiesKeeper, eurekaClient);
    }


}
