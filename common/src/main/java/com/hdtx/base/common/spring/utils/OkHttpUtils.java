package com.hdtx.base.common.spring.utils;

import com.hdtx.base.common.log.OkHttpLoggingInterceptor;
import com.hdtx.base.common.spring.ApplicationConstant;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * @Author liubin
 * @Date 2017/8/15 11:26
 */
public class OkHttpUtils {

    public static OkHttpClient.Builder okHttpClientBuilder(ApplicationConstant applicationConstant) {
        return new OkHttpClient.Builder()
                .readTimeout(applicationConstant.okHttpReadTimeout, TimeUnit.MILLISECONDS)
                .connectTimeout(applicationConstant.okHttpConnectTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(applicationConstant.okHttpWriteTimeout, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool(applicationConstant.okHttpMaxIdle,
                        applicationConstant.okHttpAliveDuration, TimeUnit.SECONDS))
                .addInterceptor(new OkHttpLoggingInterceptor(applicationConstant));
    }


}
