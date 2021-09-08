package com.hdtx.base.common.spring.ds;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import java.util.ArrayList;
import java.util.List;

public interface MetaHandlerConfigurer {

    List<MetaObjectHandler> list();

    class Default implements MetaHandlerConfigurer {

        @Override
        public List<MetaObjectHandler> list() {
            return new ArrayList<>();
        }
    }

}
