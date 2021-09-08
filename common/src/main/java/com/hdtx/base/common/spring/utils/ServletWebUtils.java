package com.hdtx.base.common.spring.utils;

import com.hdtx.base.common.spring.ApplicationConstant;
import com.hdtx.base.common.spring.ApplicationContextHolder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @Author liubin
 * @Date 2017/6/24 11:41
 */
public abstract class ServletWebUtils {

    private static final Logger logger = LoggerFactory.getLogger(ServletWebUtils.class);

    /**
     * 是否需要返回json结果
     * @param request
     * @return
     */
    public static boolean isNeedJsonResponse(HttpServletRequest request) {

        ApplicationConstant constant = ApplicationContextHolder.context.getBean(ApplicationConstant.class);
        if(!constant.webProject) {
            return true;
        }

        Enumeration<String> headerNames = request.getHeaderNames();
        boolean json = false;
        if(isAjaxRequest(request)) {
            json = true;
        } else if(headerNames != null) {
            while(headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if(headerName.equalsIgnoreCase("Content-Type") || headerName.equalsIgnoreCase("Accept")) {
                    String headerValue = request.getHeader(headerName);
                    if(StringUtils.isNotBlank(headerValue)) {
                        if(headerValue.toUpperCase().contains("JSON")) {
                            json = true;
                            break;
                        }
                    }
                }
            }
        }
        return json;
    }

    /**
     * 判断是否为Ajax请求
     * @param request   HttpServletRequest
     * @return  是true, 否false
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String requestType = request.getHeader("X-Requested-With");
        if (requestType != null && requestType.equals("XMLHttpRequest")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否为json请求头
     * @param request   HttpServletRequest
     * @return  是true, 否false
     */
    public static boolean isJsonContentType(HttpServletRequest request) {
        if(StringUtils.isBlank(request.getContentType()) ||
                request.getContentType().trim().equalsIgnoreCase("null")) return false;
        MediaType mediaType = null;
        try {
            mediaType = MediaType.parseMediaType(request.getContentType());
        } catch (Exception e) {
            logger.warn("解析content type失败" + e.getMessage());
        }
        if(mediaType == null) return false;
        return mediaType.includes(MediaType.APPLICATION_JSON) || mediaType.includes(MediaType.APPLICATION_JSON_UTF8);
    }


}
