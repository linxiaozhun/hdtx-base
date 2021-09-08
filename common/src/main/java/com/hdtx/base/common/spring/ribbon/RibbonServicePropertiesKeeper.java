package com.hdtx.base.common.spring.ribbon;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class RibbonServicePropertiesKeeper implements ApplicationListener, InitializingBean {

    private RibbonServiceProperties ribbonServiceProperties;

    private AtomicReference<Map<String, List<RibbonServiceProperties.UrlProperties>>> ribbonServiceMap = new AtomicReference<>(new HashMap<>());

    public RibbonServicePropertiesKeeper(RibbonServiceProperties ribbonServiceProperties) {
        this.ribbonServiceProperties = ribbonServiceProperties;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        initRibbonServiceMap();
    }


    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof RefreshScopeRefreshedEvent) {
            //重新初始化
            initRibbonServiceMap();
        }
    }


    private void initRibbonServiceMap() {
        ribbonServiceMap.set(
                ribbonServiceProperties.getServices().stream()
                        .collect(Collectors.toMap(
                                x -> x.getServiceName().toUpperCase(),
                                x -> x.getUrls())
                        )
        );
    }

    public AtomicReference<Map<String, List<RibbonServiceProperties.UrlProperties>>> getRibbonServiceMap() {
        return ribbonServiceMap;
    }

    public int getPeriod() {
        return ribbonServiceProperties.getPeriod() == null ? 10000 : ribbonServiceProperties.getPeriod();
    }

    public boolean isEnabled() {
        return ribbonServiceProperties.getEnabled() == null ? true : ribbonServiceProperties.getEnabled();
    }

}
