package com.hdtx.base.common.log;

import com.google.common.collect.Lists;
import com.hdtx.base.common.spring.ApplicationConstant;
import com.hdtx.base.common.utils.UrlMatcher;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

/**
 * @Author liubin
 * @Date 2017/8/24 9:32
 */
public class PerformanceLogUtil {

    private static final List<String> IGNORE_URL_LIST = Lists.newArrayList(
            "/do_not_delete/*", "/fonts/**", "/css/**", "/scmonitor/**"
    );

    private static final List<String> IGNORE_URL_PARAM_LIST = Lists.newArrayList(
            "/**/encrypt", "/**/decrypt"
    );

    //不打印url对应的日志
    private static UrlMatcher urlMatcher = new UrlMatcher(new LinkedHashSet<>(IGNORE_URL_LIST), UrlMatcher.STATIC_URL_SUFFIX_LIST);

    //打印日志的时候忽略url中的参数
    private static UrlMatcher urlParamMatcher = new UrlMatcher(new LinkedHashSet<>(IGNORE_URL_PARAM_LIST));

    private static Set<String> ignoreHeaders = new HashSet<>();


    public synchronized static void init(ApplicationConstant constant) {
        if(constant.performanceLogIgnoreUrls != null && constant.performanceLogIgnoreUrls.length > 0) {
            Set<String> antUrls = new LinkedHashSet<>(IGNORE_URL_LIST);
            for(String url : constant.performanceLogIgnoreUrls) {
                if(StringUtils.isNotBlank(url)) {
                    antUrls.add(url.trim());
                }
            }
            urlMatcher = new UrlMatcher(antUrls, UrlMatcher.STATIC_URL_SUFFIX_LIST);
        }

        if(constant.performanceLogIgnoreUrlParams != null && constant.performanceLogIgnoreUrlParams.length > 0) {
            Set<String> antUrls = new LinkedHashSet<>(IGNORE_URL_PARAM_LIST);
            for(String url : constant.performanceLogIgnoreUrlParams) {
                if(StringUtils.isNotBlank(url)) {
                    antUrls.add(url.trim());
                }
            }
            urlParamMatcher = new UrlMatcher(antUrls);
        }

        if(constant.performanceLogIgnoreHeaders != null && constant.performanceLogIgnoreHeaders.length > 0) {
            ignoreHeaders.addAll(Arrays.asList(constant.performanceLogIgnoreHeaders));
        }
    }

    public static boolean canLog(String url, PerformanceLogLevel level, Logger logger) {

        if(level.equals(PerformanceLogLevel.NONE) || !logger.isInfoEnabled()) {
            return false;
        }
        logger.warn("urlMathcher:{}", urlMatcher.getAntUrls());
        return url == null || !urlMatcher.ignore(url);
    }

    /**
     * 如果不需要记录该url参数, 返回true
     * @param url
     * @return
     */
    public static boolean isIgnoreUrlParam(String url) {
        return StringUtils.isBlank(url) || urlParamMatcher.ignore(url);
    }


    /**
     * 如果不需要记录该http头, 返回true
     * @param header
     * @return
     */
    public static boolean isIgnoreHeader(String header) {
        return StringUtils.isBlank(header) || ignoreHeaders.contains(header);
    }

    public static void main(String[] args) {

        IGNORE_URL_LIST.add("/login");
        IGNORE_URL_LIST.add("/register");
        IGNORE_URL_LIST.add("/path1/*");

        urlMatcher = new UrlMatcher(new LinkedHashSet<>(IGNORE_URL_LIST), UrlMatcher.STATIC_URL_SUFFIX_LIST);

        List<String> urls = Lists.newArrayList("/do_not_delete/check.html", "/do_not_delete", "/login",
                "/register", "/path1/123123", "/path2", "/123/encrypt", "/decrypt", "/123enrypt");

        urls.stream().filter(urlMatcher::ignore).forEach(x -> System.out.println(x));
        if(urls.stream().filter(urlMatcher::ignore).count() != 4) {
            throw new AssertionError();
        }

        String s = "http://10.100.11.90:9201/account/uploadAuditFileNew?jsonBody=%7B%22requestNo%";
        Set<String> set = new HashSet<>();
        set.add("**/uploadAuditFileNew*");
        urlMatcher = new UrlMatcher(set);
        System.out.println(urlMatcher.ignore(s));

    }

    public static String logError(Throwable ex) {
         return logError(ex, null);
    }

    /**
     *
     * @param ex
     * @param ignore 判断是不是要忽略该异常
     * @return
     */
    public static String logError(Throwable ex, Predicate<Throwable> ignore) {

        if(ex == null || (ignore != null && ignore.test(ex))) return null;

        return ex.getClass().getSimpleName() + ": " + ex.getMessage();
    }

    public static String getPathFromUrl(String url) {
        try {
            return new URL(url).getPath();
        } catch (Exception ignore) {
            return null;
        }
    }
}
