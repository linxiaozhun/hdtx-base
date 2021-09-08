package com.hdtx.base.common.spring.ds;

import org.apache.ibatis.plugin.Interceptor;

import java.util.ArrayList;
import java.util.List;

public interface MybatisInterceptorConfigurer {

    List<Interceptor> supplyInterceptors();


    class Default implements MybatisInterceptorConfigurer {

        @Override
        public List<Interceptor> supplyInterceptors() {
            return new ArrayList<>();
        }
    }

}
