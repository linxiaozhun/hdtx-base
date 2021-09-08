package com.hdtx.base.common.spring.utils;


import com.hdtx.base.common.api.ErrorInfo;
import com.hdtx.base.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Map;

/**
 * @Author liubin
 * @Date 2017/7/2 17:21
 */
@Slf4j
public abstract class SpringViewUtils {

    private static ErrorInfoConverter defaultErrorInfoConverter = new DefaultErrorInfoConverter();

    public static ModelAndView createJsonErrorView(ErrorInfo error, ErrorInfoConverter errorInfoConverter) {
        ModelAndView mav = new ModelAndView();
        MappingJackson2JsonView view = new MappingJackson2JsonView(JsonUtils.OBJECT_MAPPER);
        Map<String, Object> errorInfoMap = null;
        if(errorInfoConverter != null) {
            try {
                errorInfoMap = errorInfoConverter.convertErrorInfoToMap(error);
            } catch (Exception e) {
                log.error("", e);
            }
        }
        if(errorInfoMap == null) {
            errorInfoMap = defaultErrorInfoConverter.convertErrorInfoToMap(error);
        }
        view.setAttributesMap(errorInfoMap);
        mav.setView(view);
        return mav;
    }


}
