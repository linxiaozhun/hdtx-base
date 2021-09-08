package com.hdtx.base.common.log;

import com.google.common.collect.Lists;
import com.hdtx.base.common.spring.ApplicationConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.*;

/**
 * @Author liubin
 * @Date 2017/9/23 16:30
 */
public class SpringRequestLogFilter extends OncePerRequestFilter implements Ordered {

    private static final Logger logger = LoggerFactory.getLogger(SpringRequestLogFilter.class);

    private List<MediaType> LOG_BODY_MEDIA_TYPE = Lists.newArrayList(APPLICATION_JSON, APPLICATION_JSON_UTF8,
            APPLICATION_FORM_URLENCODED, APPLICATION_XML, TEXT_PLAIN, TEXT_XML);

    @Autowired
    private ApplicationConstant applicationConstant;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        PerformanceLogLevel performanceLogLevel = applicationConstant.determinePerformanceLogType();
        HttpServletRequest requestToUse = request;
        if(PerformanceLogUtil.canLog(request.getRequestURI(), performanceLogLevel, logger)) {

            boolean contentCache = false;
            if (needLogBody(request)) {
                contentCache = true;
                requestToUse = new ContentCachingRequestWrapper(request, calcContentLimit(request, performanceLogLevel));
            }

            Exception ex = null;
            long startTime = System.currentTimeMillis();
            Request4Log request4Log = Request4Log.create4SpringMVC(requestToUse, performanceLogLevel);
            logger.info(request4Log.toString());

            try {

                filterChain.doFilter(requestToUse, response);
            } catch (Exception e) {

                ex = e;
                throw e;
            }  finally {

                if (!isAsyncStarted(requestToUse)) {
                    long costTime = System.currentTimeMillis() - startTime;
                    String requestBody = null;

                    try {
                        ContentCachingRequestWrapper wrapper =
                                WebUtils.getNativeRequest(requestToUse, ContentCachingRequestWrapper.class);
                        if (contentCache && wrapper != null) {
                            byte[] buf = wrapper.getContentAsByteArray();
                            if(buf.length > 0) {
                                requestBody = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                            }
                        }
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                    Response4Log logResp = Response4Log.create4SpringMVC(performanceLogLevel, request,
                            costTime, PerformanceLogUtil.logError(ex), requestBody, response);
                    logger.info(logResp.toString());
                }
            }
        } else {

            filterChain.doFilter(requestToUse, response);
        }

    }

    private int calcContentLimit(HttpServletRequest request, PerformanceLogLevel performanceLogLevel) {
        int contentLength = request.getContentLength();
        int limit = contentLength >= 0 ? contentLength : 1024;
        if(PerformanceLogLevel.SIMPLE.equals(performanceLogLevel)) {
            limit = Math.min(limit, PerformanceLog.SIMPLE_MAX_SIZE);
        }
        return limit;
    }

    /**
     * 如果不是get/head/options请求, 并且消息头不是和文件或二进制有关的, 就记录消息体.
     * @param request
     * @return
     */
    private boolean needLogBody(HttpServletRequest request) {

        boolean isFirstRequest = !isAsyncDispatch(request);

        if (!isFirstRequest || (request instanceof ContentCachingRequestWrapper)) return false;

        if(!"PUT".equalsIgnoreCase(request.getMethod()) && !"POST".equalsIgnoreCase(request.getMethod())
                && !"DELETE".equalsIgnoreCase(request.getMethod())) return false;

        try {
            String contentType = request.getContentType();
            if(StringUtils.isNotBlank(contentType) && !contentType.trim().equalsIgnoreCase("null")) {
                MediaType mediaType = MediaType.parseMediaType(contentType);
                if(LOG_BODY_MEDIA_TYPE.stream().noneMatch(mediaType::includes)) {
                    return false;
                }
            }
        } catch (Exception e) {
            logger.warn("", e);
        }

        return true;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
