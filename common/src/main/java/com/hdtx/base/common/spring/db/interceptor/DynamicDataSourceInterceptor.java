package com.hdtx.base.common.spring.db.interceptor;


import com.hdtx.base.common.spring.db.annotation.DynamicDataSource;
import com.hdtx.base.common.spring.db.constant.DynamicDataSourceThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author XiaoLin
 * @date 2021年12月10日
 * @time 11:08
 */
@Aspect
@Slf4j
public class DynamicDataSourceInterceptor {


    @Around("@annotation(dynamicDataSource)")
    public Object aroundAOP(ProceedingJoinPoint proceedingJoinPoint, DynamicDataSource dynamicDataSource) {
        try {
            DynamicDataSourceThreadLocal.setDataSourceKey(dynamicDataSource.dataSourceKey());
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("切换数据源异常:", throwable);
            throwable.printStackTrace();
        } finally {
            DynamicDataSourceThreadLocal.clearDataSource();
        }
        return null;
    }





}
