package com.hdtx.base.common.spring.feign;

import com.hdtx.base.common.api.CommonErrorCode;
import com.hdtx.base.common.api.ErrorCode;
import com.hdtx.base.common.api.ErrorInfo;
import com.hdtx.base.common.exception.RemoteCallException;
import com.hdtx.base.utils.JsonUtils;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * @Author liubin
 * @Date 2017/6/16 14:34
 */
public class FeignErrorHandler implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {

        ErrorCode errorCode = CommonErrorCode.fromHttpStatus(response.status());
        if(errorCode.equals(CommonErrorCode.INTERNAL_ERROR)) {
            errorCode = CommonErrorCode.REQUEST_SERVICE_ERROR;
        }

        ErrorInfo error = new ErrorInfo(errorCode, "");

        try {
            if (response.body() != null) {
                String body = Util.toString(response.body().asReader());
                if(StringUtils.isNotBlank(body)) {
                    try {
                        body = body.trim();
                        error = JsonUtils.json2Object(body, ErrorInfo.class);
                    } catch (Exception ignore) {
                    }
                }
            }
        } catch (IOException ignored) { // NOPMD
        }

        return new RemoteCallException(error);
    }


}
