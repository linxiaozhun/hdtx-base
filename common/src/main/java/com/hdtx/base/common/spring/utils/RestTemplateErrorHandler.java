package com.hdtx.base.common.spring.utils;

import com.hdtx.base.common.api.CommonErrorCode;
import com.hdtx.base.common.api.ErrorCode;
import com.hdtx.base.common.api.ErrorInfo;
import com.hdtx.base.common.exception.RemoteCallException;
import com.hdtx.base.common.utils.JsonUtils;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @Author liubin
 * @Date 2017/5/15 10:34
 */
public class RestTemplateErrorHandler extends DefaultResponseErrorHandler {

    protected Logger logger = LoggerFactory.getLogger(RestTemplateErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {

        int statusCode = response.getRawStatusCode();
        return ErrorCode.isBusinessStatus(statusCode) || (statusCode >= 400 && statusCode < 600);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        Charset charset = getCharset(response);
        byte[] responseBody = getResponseBody(response);
        String responseText = null;
        if(responseBody.length > 0) {
            responseText = new String(responseBody, charset).trim();
        }

        CommonErrorCode errorCode = CommonErrorCode.fromHttpStatus(response.getRawStatusCode());
        if(errorCode.equals(CommonErrorCode.INTERNAL_ERROR)) {
            errorCode = CommonErrorCode.REQUEST_SERVICE_ERROR;
        }
        ErrorInfo error = new ErrorInfo(errorCode, "", errorCode.getMessage());
        boolean setText = false;
        if(StringUtils.isNotBlank(responseText)) {
            try {
                error = JsonUtils.json2Object(responseText, ErrorInfo.class);
            } catch (Exception ignore) {
                setText = true;
            }
        }

        throw new RemoteCallException(error, setText ? responseText : "");
    }


    @Override
    protected Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        Charset charset = (contentType != null && contentType.getCharset() != null) ?
                contentType.getCharset() : Charset.forName("UTF-8");
        if(charset == null) {
            charset = Charset.forName("UTF-8");
        }
        return charset;
    }

}
