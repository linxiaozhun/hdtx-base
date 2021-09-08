package com.hdtx.base.common.spring.mybatis;

import com.baomidou.mybatisplus.core.handlers.StrictFill;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

@ConfigurationProperties("mybatis-meta")
@Getter
@Setter
public class MybatisMateProperties {

    @NestedConfigurationProperty
    private List<StrictFill> insertMetaObject;

    @NestedConfigurationProperty
    private List<StrictFill> updateMetaObject;

   /* @Getter
    @Setter
    public static  class MetaObject{
        private String fieldName;

        private String fieldType;

        private String fieldValue;
    }*/

}
