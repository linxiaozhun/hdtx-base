package com.hdtx.base.utils.spring;

import com.hdtx.base.utils.JsonUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * @Author liubin
 * @Date 2017/6/15 10:11
 */
public class RestTemplateUtils {

    public static HttpEntity<String> createJsonEntity(Object object) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new HttpEntity<>(JsonUtils.object2Json(object), requestHeaders);
    }


}
