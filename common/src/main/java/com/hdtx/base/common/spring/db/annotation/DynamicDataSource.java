package com.hdtx.base.common.spring.db.annotation;

import com.hdtx.base.common.spring.db.constant.ReadWriteType;

import java.lang.annotation.*;

@Documented
@Target(value = {ElementType.METHOD,ElementType.TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicDataSource {

    String dataSourceKey() default "com.hdtx.datasource";

    ReadWriteType readWriteType() default ReadWriteType.DB_READ_AND_WRITE;

}
