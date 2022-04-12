package com.hdtx.base.common.spring.utils;

import com.hdtx.base.common.api.ErrorInfo;
import com.hdtx.base.common.utils.JsonUtils;


import java.util.Map;

/**
 * @Author liubin
 * @Date 2017/8/19 15:07
 */
public class DefaultErrorInfoConverter implements ErrorInfoConverter {
    @Override
    public Map<String, Object> convertErrorInfoToMap(ErrorInfo errorInfo) {
        return JsonUtils.object2Map(errorInfo);
    }

    @Override
    public Map<String, Object> convertErrorToMap(Object obj) {
        return JsonUtils.object2Map(obj);
    }
}
