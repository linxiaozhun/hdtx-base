package com.hdtx.base.common.log;


import com.hdtx.base.utils.JsonUtils;
import feign.Request;
import okio.Buffer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhoumeihua on 2017/7/18.
 */
public final class Request4Log extends PerformanceLog {

    private static final Logger logger = LoggerFactory.getLogger(Request4Log.class);

    public static final String CLIENT_IP_HEADER = "X-Forwarded-For";

    /**
     * 请求URI
     */
    private String uri;

    /**
     * 请求http消息类型
     */
    private String httpMethod;

    /**
     * 请求头
     */
    private String header;

    /**
     * 请求参数(httpServletRequest.getParameterMap获得)
     */
    private String param;

    /**
     * 请求消息体
     */
    private String body;

    /**
     * client IP address, only in spring mvc request.
     */
    private String clientIp;


    public Request4Log() {}

    public Request4Log(PerformanceLogType type, PerformanceLogLevel performanceLogLevel) {
        super(type, performanceLogLevel);
    }


    public static Request4Log create4SpringMVC(HttpServletRequest request, PerformanceLogLevel performanceLogLevel) {

        Request4Log request4Log = new Request4Log(PerformanceLogType.SPRING_REQ, performanceLogLevel);

        swallowException(() -> {

            request4Log.body = "";
            request4Log.uri = trimRequestUri(request.getRequestURI());
            request4Log.httpMethod = request.getMethod();
            if(!PerformanceLogLevel.MINIMUM.equals(performanceLogLevel)) {

                if(!PerformanceLogUtil.isIgnoreUrlParam(request4Log.uri)) {
                    Map<String, String[]> parameterMap = request.getParameterMap();
                    if(parameterMap != null) {
                        request4Log.param = JsonUtils.object2Json(parameterMap);
                    }
                }

                request4Log.clientIp = collectClientIp(request);
                Map<String, String> headerMap = extractHeaderToMap(request);
                if(headerMap != null) {
                    request4Log.header = JsonUtils.object2Json(headerMap);
                }
                request4Log.simplifyLogIfNecessary(performanceLogLevel);

            }
        });

        return request4Log;
    }

    @Deprecated
    public static Request4Log create4RestTemplate(HttpRequest request, byte[] body, PerformanceLogLevel performanceLogLevel) {

        Request4Log request4Log = new Request4Log(PerformanceLogType.REST_REQ, performanceLogLevel);

        swallowException(() -> {

            request4Log.param = "";
            request4Log.uri = trimRequestUri(request.getURI().toString());
            request4Log.httpMethod = request.getMethod().name();
            if(!PerformanceLogLevel.MINIMUM.equals(performanceLogLevel)) {

                if(!PerformanceLogUtil.isIgnoreUrlParam(request4Log.uri)) {
                    if(body != null && body.length > 0) {
                        try {
                            request4Log.body = new String(body, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            logger.error("", e);
                        }
                    }
                }
                if(request.getHeaders() != null) {
                    request4Log.header = JsonUtils.object2Json(request.getHeaders());
                }
                request4Log.simplifyLogIfNecessary(performanceLogLevel);

            }

        });

        return request4Log;
    }

    @Deprecated
    public static Request4Log create4Feign(Request request, PerformanceLogLevel performanceLogLevel) {

        Request4Log request4Log = new Request4Log(PerformanceLogType.FEIGN_REQ, performanceLogLevel);

        swallowException(() -> {
            request4Log.param = "";
            request4Log.uri = trimRequestUri(request.url());
            request4Log.httpMethod = request.method();
            if(!PerformanceLogLevel.MINIMUM.equals(performanceLogLevel)) {

                if(!PerformanceLogUtil.isIgnoreUrlParam(request4Log.uri)) {
                    byte[] body = request.body();
                    if (body != null && body.length > 0) {
                        try {
                            request4Log.body = new String(body, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            logger.error("", e);
                        }
                    }
                    if (request.headers() != null) {
                        //headers is unmodifiable
                        request4Log.header = JsonUtils.object2Json(request.headers());
                    }
                    request4Log.simplifyLogIfNecessary(performanceLogLevel);
                }

            }

        });

        return request4Log;
    }

    public static Request4Log create4OkHttp(okhttp3.Request request, PerformanceLogLevel performanceLogLevel) {

        Request4Log request4Log = new Request4Log(PerformanceLogType.OKHTTP_REQ, performanceLogLevel);

        swallowException(() -> {
            request4Log.param = "";
            request4Log.uri = trimRequestUri(request.url().toString());
            request4Log.httpMethod = request.method();
            if(!PerformanceLogLevel.MINIMUM.equals(performanceLogLevel)) {

                String urlPath = PerformanceLogUtil.getPathFromUrl(request4Log.uri);
                if(urlPath == null || !PerformanceLogUtil.isIgnoreUrlParam(urlPath)) {
                    request4Log.body = bodyToString(request);
                }
                if(request.headers() != null) {
                    request4Log.header = JsonUtils.object2Json(removeIgnoreHeader(request.headers().toMultimap()));
                }
                request4Log.simplifyLogIfNecessary(performanceLogLevel);
            }

        });

        return request4Log;
    }

    private static String bodyToString(final okhttp3.Request request){

        if(request.body() != null) {
            try {
                final okhttp3.Request copy = request.newBuilder().build();
                final Buffer buffer = new Buffer();
                copy.body().writeTo(buffer);
                return buffer.readUtf8();
            } catch (final IOException e) {
                logger.error("", e);
            }
        }
        return null;
    }

    private void simplifyLogIfNecessary(PerformanceLogLevel performanceLogLevel) {
        if(PerformanceLogLevel.SIMPLE.equals(performanceLogLevel)) {
            if(this.body != null && this.body.length() > SIMPLE_MAX_SIZE) {
                this.body = this.body.substring(0, SIMPLE_MAX_SIZE);
            }
            if(this.header != null && this.header.length() > SIMPLE_MAX_SIZE) {
                this.header = this.header.substring(0, SIMPLE_MAX_SIZE);
            }
            if(this.param != null && this.param.length() > SIMPLE_MAX_SIZE) {
                this.param = this.param.substring(0, SIMPLE_MAX_SIZE);
            }
        }
    }

    private static Map<String, String> extractHeaderToMap(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return null;
        }
        Map<String, String> headerMap = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            if(!PerformanceLogUtil.isIgnoreHeader(header)) {
                headerMap.put(header, request.getHeader(header));
            }
        }
        return headerMap;
    }

    private static String collectClientIp(HttpServletRequest request) {
        String clientIps = request.getHeader(CLIENT_IP_HEADER);
        if(StringUtils.isNotBlank(clientIps)) {
            String[] clientIpArray = clientIps.split(",");
            if (clientIpArray.length > 0) {
                return StringUtils.isBlank(clientIpArray[0]) ? null : clientIpArray[0].trim();
            }
        }
        return null;
    }



    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
}
