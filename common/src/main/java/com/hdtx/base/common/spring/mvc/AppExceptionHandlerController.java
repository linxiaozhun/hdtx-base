package com.hdtx.base.common.spring.mvc;

import com.google.common.base.Joiner;
import com.hdtx.base.apiutils.ResultBody;
import com.hdtx.base.apiutils.api.BaseErrorInfo;
import com.hdtx.base.common.api.CommonErrorCode;
import com.hdtx.base.common.api.ErrorCode;
import com.hdtx.base.common.api.ErrorInfo;
import com.hdtx.base.common.exception.AppBusinessException;
import com.hdtx.base.common.exception.BaseException;
import com.hdtx.base.common.exception.ServiceUnavailableException;
import com.hdtx.base.common.spring.utils.SpringViewUtils;
import com.hdtx.base.common.utils.JsonUtils;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.exception.HystrixTimeoutException;

import com.hdtx.base.common.exception.RemoteCallException;
import com.hdtx.base.common.spring.ApplicationConstant;
import com.hdtx.base.common.spring.utils.DefaultErrorInfoConverter;
import com.hdtx.base.common.spring.utils.ErrorInfoConverter;
import com.hdtx.base.common.spring.utils.ServletWebUtils;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统一异常处理
 *
 * @Author liubin
 * @Date 2017/5/15 15:02
 */
@ControllerAdvice
@Order(-999)
public class AppExceptionHandlerController extends CustomResponseEntityExceptionHandler {

    protected Logger logger = LoggerFactory.getLogger(AppExceptionHandlerController.class);

    @Autowired(required = false)
    private ApplicationConstant applicationConstant;

    @Autowired(required = false)
    private ErrorInfoConverter errorInfoConverter;

    @Override
    protected ModelAndView handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
        if (webRequest instanceof ServletWebRequest) {
            ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
            HttpServletRequest request = servletRequest.getNativeRequest(HttpServletRequest.class);
            HttpServletResponse response = servletRequest.getNativeResponse(HttpServletResponse.class);

            ErrorCode errorCode = CommonErrorCode.fromHttpStatus(status.value());
            List<ObjectError> allErrors = ex.getAllErrors();
            String errorMsg = extractErrorMessageFromObjectErrors(allErrors, errorCode.getMessage());

            return createModeAndViewResponse(errorCode, request, response, errorMsg);
        } else {
            return super.handleBindException(ex, headers, status, webRequest);
        }

    }

    @Override
    protected ModelAndView handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
        if (webRequest instanceof ServletWebRequest) {
            ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
            HttpServletRequest request = servletRequest.getNativeRequest(HttpServletRequest.class);
            HttpServletResponse response = servletRequest.getNativeResponse(HttpServletResponse.class);

            ErrorCode errorCode = CommonErrorCode.fromHttpStatus(status.value());
            List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
            String errorMsg = extractErrorMessageFromObjectErrors(allErrors, errorCode.getMessage());

            return createModeAndViewResponse(errorCode, request, response, errorMsg);
        } else {
            return super.handleMethodArgumentNotValid(ex, headers, status, webRequest);
        }
    }

    @ExceptionHandler(value = ServiceUnavailableException.class)
    public ModelAndView handleServiceUnavailableException(HttpServletRequest request, HttpServletResponse response, ServiceUnavailableException e) {

        logger.error(e.getMessage(), e);
        return createModeAndViewResponse(e.getHttpStatus(), e.getHttpStatus(), request, response, e.getMessage());
    }

    @ExceptionHandler(value = RemoteCallException.class)
    public ModelAndView handleRemoteCallException(HttpServletRequest request, HttpServletResponse response, RemoteCallException e) {

        //系统异常用error级别, 否则warn级别
        if (e.isSystemException()) {
            logger.error(e.getMessage(), e);
        } else {
            logger.warn(e.getMessage(), e);
        }
        return createModeAndViewResponse(e.getOriginError().getCode(), e.getHttpStatus(), request, response, e.getOriginError().getMessage());
    }

    @ExceptionHandler(value = HystrixTimeoutException.class)
    public ModelAndView handleHystrixTimeoutException(HttpServletRequest request, HttpServletResponse response, HystrixTimeoutException e) {

        logger.error(e.getMessage(), e);
        ErrorCode errorCode = CommonErrorCode.GATEWAY_TIMEOUT;
        return createModeAndViewResponse(errorCode, request, response, e.getMessage());
    }

    @ExceptionHandler(value = HystrixRuntimeException.class)
    public ModelAndView handleHystrixRuntimeException(HttpServletRequest request, HttpServletResponse response, HystrixRuntimeException e) {

        logger.error("Hystrix Command 运行报错: " + e.getMessage(), e);
        ErrorCode errorCode = CommonErrorCode.INTERNAL_ERROR;
        return createModeAndViewResponse(errorCode, request, response, e.getMessage());
    }


    @ExceptionHandler(value = AppBusinessException.class)
    public ModelAndView handleAppBusinessException(HttpServletRequest request, HttpServletResponse response, AppBusinessException e) {

        //业务异常
        return createModeAndViewResponse(e.getHttpStatus(), e.getHttpStatus(), request, response, e.getMessage());
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ModelAndView handleAppBusinessException(HttpServletRequest request, HttpServletResponse response, IllegalArgumentException e) {
        //业务异常
        return createModeAndViewResponse(CommonErrorCode.INTERNAL_ERROR.getCode(), 200, request, response, e.getMessage());
    }

    @ExceptionHandler(value = BaseException.class)
    public ModelAndView handleBaseException(HttpServletRequest request, HttpServletResponse response, AppBusinessException e) {

        //框架异常
        logger.error(e.getMessage(), e);
        ErrorCode errorCode = CommonErrorCode.INTERNAL_ERROR;

        return createModeAndViewResponse(errorCode, request, response, e.getMessage());
    }


    @ExceptionHandler(value = Exception.class)
    public ModelAndView handleException(HttpServletRequest request, HttpServletResponse response, Exception e) {

        logger.error("服务器发生错误: " + e.getMessage(), e);
        ErrorCode errorCode = CommonErrorCode.INTERNAL_ERROR;
        return createModeAndViewResponse(errorCode, request, response, getExceptionMessage(ServletWebUtils.isNeedJsonResponse(request), e));

    }


    protected ModelAndView createModeAndViewResponse(ErrorCode errorCode, HttpServletRequest request, HttpServletResponse response, String message) {
        return createModeAndViewResponse(errorCode.getCode(), errorCode.getStatus(), request, response, message);
    }

    @Deprecated
    protected ModelAndView createModeAndViewResponse(String code, int httpStatus, HttpServletRequest request, HttpServletResponse response, String message) {

        boolean needJsonResponse = ServletWebUtils.isNeedJsonResponse(request);
        if (!isNeedDetailErrorMessage(needJsonResponse) && !needJsonResponse) {
            message = "系统出现错误";
        }
        ErrorInfo error = new ErrorInfo(code, request.getRequestURI(), message, httpStatus);

        if (errorInfoConverter == null) {
            errorInfoConverter = new DefaultErrorInfoConverter();
        }
        if (errorInfoConverter.modifyHttpStatus()) {
            response.setStatus(httpStatus);
        }

        if (needJsonResponse) {
            return SpringViewUtils.createJsonErrorView(error, errorInfoConverter);
        } else {
            return createErrorModelAndView(httpStatus, error);
        }

    }

    protected ModelAndView createModeAndViewResponse(int code, int httpStatus, HttpServletRequest request, HttpServletResponse response, String message) {

        boolean needJsonResponse = ServletWebUtils.isNeedJsonResponse(request);
        if (!isNeedDetailErrorMessage(needJsonResponse) && !needJsonResponse) {
            message = "系统出现错误";
        }
        ResultBody resultBody= ResultBody.create(message,code);

        if (errorInfoConverter == null) {
            errorInfoConverter = new DefaultErrorInfoConverter();
        }
        if (errorInfoConverter.modifyHttpStatus()) {
            response.setStatus(httpStatus==500? HttpStatus.OK.value():httpStatus);
        }

        if (needJsonResponse) {
            return SpringViewUtils.createJsonErrorView(resultBody, errorInfoConverter);
        } else {
            return createErrorModelAndView(httpStatus, resultBody);
        }

    }


    @Deprecated
    protected ModelAndView createErrorModelAndView(int httpStatus, ErrorInfo error) {
        String viewName = "200";
        if (httpStatus == 401 || httpStatus == 404) {
            viewName = String.valueOf(httpStatus);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("errorInfo", error);
        String errorMessage = "";
        if (isNeedDetailErrorMessage(false)) {
            errorMessage = error.getMessage();
        }
        map.put("errorMessage", errorMessage);
        return new ModelAndView(viewName, map);
    }


    protected ModelAndView createErrorModelAndView(int httpStatus, ResultBody error) {
        String viewName = "200";
        if (httpStatus == 401 || httpStatus == 404) {
            viewName = String.valueOf(httpStatus);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("errorInfo", error);
        String errorMessage = "";
        if (isNeedDetailErrorMessage(false)) {
            errorMessage = error.getMsg();
        }
        map.put("errorMessage", errorMessage);
        return new ModelAndView(viewName, map);
    }


    private String extractErrorMessageFromObjectErrors(List<ObjectError> allErrors, String defaultMessage) {
        if (allErrors == null || allErrors.isEmpty()) {
            return defaultMessage;
        } else {
            List<String> errorMessages = allErrors.stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            return Joiner.on(",").skipNulls().join(errorMessages);
        }
    }

    @Override
    protected ModelAndView handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            webRequest.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        ErrorCode errorCode = CommonErrorCode.fromHttpStatus(status.value());
        String errorMsg = errorCode.getMessage();
        if (!(ex instanceof HttpRequestMethodNotSupportedException)) {
            //忽略特定异常的错误消息
            logger.error(ex.getMessage());
        }
        if (webRequest instanceof ServletWebRequest) {
            ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
            HttpServletRequest request = servletRequest.getNativeRequest(HttpServletRequest.class);
            HttpServletResponse response = servletRequest.getNativeResponse(HttpServletResponse.class);
            return createModeAndViewResponse(errorCode, request, response, errorMsg);
        } else {
            return SpringViewUtils.createJsonErrorView(
                    new ErrorInfo(errorCode, "", errorMsg),
                    errorInfoConverter);
        }
    }

    /**
     * 开发环境打印详细日志
     *
     * @param needJson
     * @param e
     * @return
     */
    private String getExceptionMessage(boolean needJson, Exception e) {
        return isNeedDetailErrorMessage(needJson) ? ExceptionUtils.getStackTrace(e) : e.getMessage();
    }

    /**
     * 是否需要返回详细错误信息
     *
     * @param needJson
     * @return
     */
    private boolean isNeedDetailErrorMessage(boolean needJson) {
        return !(applicationConstant.isProdProfile() || needJson);
    }

}