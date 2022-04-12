package com.hdtx.base.common.spring.ribbon;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class EurekaInstanceToServiceFinder implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(EurekaInstanceToServiceFinder.class);


    private RibbonServicePropertiesKeeper ribbonServicePropertiesKeeper;

    private EurekaClient eurekaClient;

    private ScheduledExecutorService scheduledExecutorService;

    private AtomicBoolean taskRunning = new AtomicBoolean(false);

    private AtomicReference<Map<String, String>> instanceToServiceMap = new AtomicReference<>(new HashMap<>());

    public EurekaInstanceToServiceFinder(RibbonServicePropertiesKeeper ribbonServicePropertiesKeeper, EurekaClient eurekaClient) {
        this.ribbonServicePropertiesKeeper = ribbonServicePropertiesKeeper;
        this.eurekaClient = eurekaClient;
    }


    @Override
    public void destroy() throws Exception {
        scheduledExecutorService.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        logger.info("For ribbon url timeout feature, InstanceToServiceRefreshTask start running");
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //early initialize
        eurekaClient.getApplications();
        scheduledExecutorService.scheduleWithFixedDelay(new InstanceToServiceRefreshTask(), 10000L,
                ribbonServicePropertiesKeeper.getPeriod(), TimeUnit.MILLISECONDS);

    }

    /**
     * 根据ip和端口查找服务名
     * @param ip 如果ip小于0, 设置为80
     * @param port
     * @return
     */
    public String findServiceNameByInstance(String ip, int port) {

        if(port < 0) {
            port = 80;
        }

        return instanceToServiceMap.get().get(ip + ":" + port);

    }


    class InstanceToServiceRefreshTask implements Runnable {

        @Override
        public void run() {

            if(!taskRunning.compareAndSet(false, true) || !ribbonServicePropertiesKeeper.isEnabled()) {
                return;
            }

            try {

                Set<String> serviceNames = ribbonServicePropertiesKeeper.getRibbonServiceMap().get().keySet();

                if(serviceNames.isEmpty()) {
                    return;
                }

                Applications applications = eurekaClient.getApplications();
                Map<String, String> tempMap = applications.getRegisteredApplications().stream()
                        .filter(x -> serviceNames.contains(x.getName()))
                        .map(Application::getInstances)
                        .flatMap(List::stream)
                        .collect(Collectors.toMap(x -> x.getIPAddr() + ":" + x.getPort(), InstanceInfo::getAppName, (x, y) -> x));

                instanceToServiceMap.set(tempMap);

            } catch (Exception e) {

                logger.error("", e);

            } finally {
                taskRunning.set(false);
            }

        }
    }

}
