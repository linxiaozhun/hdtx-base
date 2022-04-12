package com.hdtx.base.common.spring.utils;

import com.hdtx.base.apiutils.api.CommonError;
import com.hdtx.base.apiutils.exception.AppBusinessException;
import com.hdtx.base.apiutils.exception.ServiceUnavailableException;
import com.hdtx.base.common.spring.ApplicationConstant;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.*;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author liubin
 * @Date 2017/5/15 10:33
 */
public class EnhancedRestTemplate extends RestTemplate {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedRestTemplate.class);

    public EnhancedRestTemplate() {
        super();
    }

    public EnhancedRestTemplate(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
    }

    public EnhancedRestTemplate(List<HttpMessageConverter<?>> messageConverters) {
        super(messageConverters);
    }

    @Override
    protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback,
                              ResponseExtractor<T> responseExtractor) throws RestClientException {
        try {
            return super.doExecute(url, method, requestCallback, responseExtractor);
        } catch (ResourceAccessException e) {

            throw new ServiceUnavailableException(e.getMessage());

        } catch (IllegalStateException e) {

            if(e.getMessage().contains("No instances")) {
                //目标服务没启动的时候RibbonLoadBalancerClient.execute会抛出IllegalStateException
                //转化成ServiceUnavailableException
                throw new ServiceUnavailableException(e.getMessage());
            } else {
                throw e;
            }
        }
    }


    public static EnhancedRestTemplate assembleRestTemplate(
            MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter, OkHttpClient okHttpClient) {

        EnhancedRestTemplate restTemplate = createRestTemplate(okHttpClient);

        //自定义异常处理
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        messageConverters.removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);
        messageConverters.add(mappingJackson2HttpMessageConverter);
        return restTemplate;
    }

    private static EnhancedRestTemplate createRestTemplate(OkHttpClient okHttpClient) {
        EnhancedRestTemplate restTemplate = new EnhancedRestTemplate(new OkHttp3ClientHttpRequestFactory(okHttpClient));
        return restTemplate;
    }

    private static OkHttpClient rawOkHttpClient = null;

    private static MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = null;

    private static ApplicationConstant applicationConstant = null;

    private static AtomicBoolean init = new AtomicBoolean(false);

    /**
     * builder可以复用, 能够重用socket连接, 不能大量使用assembleRestTemplate创建过多连接.
     * @return
     */
    public static Builder newBuilder() {
        if(!init.get()) {
            throw new AppBusinessException(CommonError.INTERNAL_ERROR, "EnhancedRestTemplate.newBuilder初始化失败, 需要等项目启动完成再调用该方法");
        }
        return new Builder();
    }

    public static synchronized void initBuilderInfo(ApplicationContext context) {
        if(init.compareAndSet(false, true)) {
            applicationConstant = context.getBean(ApplicationConstant.class);
            mappingJackson2HttpMessageConverter = context.getBean(MappingJackson2HttpMessageConverter.class);
            rawOkHttpClient = OkHttpUtils.okHttpClientBuilder(EnhancedRestTemplate.applicationConstant).build();
        }
    }

    public static final class Builder {

        private OkHttpClient.Builder builder = null;

        private long okHttpReadTimeout;

        private long okHttpConnectTimeout;

        private long okHttpWriteTimeout;


        public Builder() {
            init();
        }

        private void init() {
            try {
                this.builder = rawOkHttpClient.newBuilder();

                this.okHttpReadTimeout = applicationConstant.okHttpReadTimeout;
                this.okHttpConnectTimeout = applicationConstant.okHttpConnectTimeout;
                this.okHttpWriteTimeout = applicationConstant.okHttpWriteTimeout;
            } catch (Exception e) {
                logger.error("EnhancedRestTemplate.newBuilder初始化失败, 需要等项目启动完成再调用该方法");
                throw e;
            }
        }


        /**
         * @param readTimeout 读取超时时间, 单位毫秒
         * @return
         */
        public Builder readTimeout(long readTimeout) {
            this.okHttpReadTimeout = readTimeout;
            return this;
        }

        /**
         * @param connectTimeout 连接超时时间, 单位毫秒
         * @return
         */
        public Builder connectTimeout(long connectTimeout) {
            this.okHttpConnectTimeout = connectTimeout;
            return this;
        }

        /**
         * @param writeTimeout 写入超时时间, 单位毫秒
         * @return
         */
        public Builder writeTimeout(long writeTimeout) {
            this.okHttpWriteTimeout = writeTimeout;
            return this;
        }

        public EnhancedRestTemplate build() {
            return assembleRestTemplate(mappingJackson2HttpMessageConverter,
                    builder.connectTimeout(this.okHttpConnectTimeout, TimeUnit.MILLISECONDS)
                            .readTimeout(this.okHttpReadTimeout, TimeUnit.MILLISECONDS)
                            .writeTimeout(this.okHttpWriteTimeout, TimeUnit.MILLISECONDS).build());
        }

    }

}
