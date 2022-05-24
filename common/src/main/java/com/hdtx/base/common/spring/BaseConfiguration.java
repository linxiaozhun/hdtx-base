package com.hdtx.base.common.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdtx.base.common.spring.refresh.TdContextRefresher;
import com.hdtx.base.common.spring.utils.EnhancedRestTemplate;
import com.hdtx.base.common.spring.utils.OkHttpUtils;
import com.hdtx.base.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * @Author liubin
 * @Date 2017/5/15 15:03
 */
@EnableHystrix
@MapperScan(basePackages = {"com.hdtx.**.dao","com.hdtx.**.mapper"})
@ComponentScan({"com.hdtx.**.service", "com.hdtx.**.fallback", "com.hdtx.**.component","com.hdtx.**","com.hdtx.**.handle"})
@Slf4j
public class BaseConfiguration{

    @Bean
    public ApplicationConstant applicationConstant() {
        return new ApplicationConstant();
    }

    //customize object mapper
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JsonUtils.OBJECT_MAPPER;
    }

    @Bean
    public ApplicationContextHolder applicationContextHolder(ApplicationConstant applicationConstant) {
        ApplicationContextHolder applicationContextHolder = ApplicationContextHolder.getInstance();
        applicationContextHolder.init(applicationConstant);
        log.info("init applicationContextHolder 完成！！");
        return applicationContextHolder;
    }


    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter(objectMapper());
    }

    @Primary
    @Bean
    public OkHttpClient okHttpClient(ApplicationConstant applicationConstant){
        return OkHttpUtils.okHttpClientBuilder(applicationConstant).build();
    }

    @LoadBalanced
    @Bean(name = "serviceRestTemplate")
    public RestTemplate restTemplate(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter,
                                     OkHttpClient okHttpClient) {

        return EnhancedRestTemplate.assembleRestTemplate(mappingJackson2HttpMessageConverter, okHttpClient);
    }

    @Primary
    @Bean(name = "rawRestTemplate")
    public RestTemplate rawRestTemplate(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter,
                                        OkHttpClient okHttpClient) {

        return EnhancedRestTemplate.assembleRestTemplate(mappingJackson2HttpMessageConverter, okHttpClient);
    }

    @Bean
    public ContextRefresher contextRefresher(ConfigurableApplicationContext context,
                                             RefreshScope scope) {
        return new TdContextRefresher(context, scope);
    }



//    @Bean
//    public SpanLogger slf4jSpanLogger(SleuthSlf4jProperties sleuthSlf4jProperties) {
//        // Sets up MDC entries X-B3-TraceId and X-B3-SpanId
//        return new TdSlf4jSpanLogger(sleuthSlf4jProperties.getNameSkipPattern());
//    }



}