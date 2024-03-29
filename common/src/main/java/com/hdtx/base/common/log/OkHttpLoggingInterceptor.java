package com.hdtx.base.common.log;

import com.hdtx.base.common.spring.ApplicationConstant;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @Author liubin
 * @Date 2017/8/15 15:55
 */
public class OkHttpLoggingInterceptor implements Interceptor {

    final static Logger logger = LoggerFactory.getLogger(OkHttpLoggingInterceptor.class);

    private ApplicationConstant applicationConstant;

    public OkHttpLoggingInterceptor(ApplicationConstant applicationConstant) {
        this.applicationConstant = applicationConstant;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        PerformanceLogLevel performanceLogLevel = applicationConstant.determinePerformanceLogType();
        Request request = chain.request();
        logger.warn("拦截URI:{}",request.url().uri().toString());
        if(!PerformanceLogUtil.canLog(request.url().uri().toString(), performanceLogLevel, logger)) {
            return chain.proceed(request);
        } else {
            long startTime = System.currentTimeMillis();
            Request4Log request4Log = Request4Log.create4OkHttp(request, performanceLogLevel);
            logger.info(request4Log.toString());
            Exception error = null;
            Response response = null;
            try {
                response = chain.proceed(request);
                return response;
            } catch (Exception e) {
                error = e;
                throw e;
            } finally {
                long costTime = System.currentTimeMillis() - startTime;
                Response4Log logResp = Response4Log.create4OkHttp(performanceLogLevel, request.url().toString(),
                        costTime, PerformanceLogUtil.logError(error), response);
                logger.info(logResp.toString());
            }
        }
    }

}
