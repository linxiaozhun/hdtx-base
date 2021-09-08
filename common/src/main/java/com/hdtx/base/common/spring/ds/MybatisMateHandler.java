package com.hdtx.base.common.spring.ds;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.hdtx.base.common.spring.mybatis.MybatisMateProperties;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;


public class MybatisMateHandler implements MetaObjectHandler {

    private MybatisMateProperties mybatisMateProperties;

    private static final Logger logger = LoggerFactory.getLogger(MybatisMateHandler.class);

    public MybatisMateHandler(MybatisMateProperties mybatisMateProperties) {
        this.mybatisMateProperties = mybatisMateProperties;
    }

    /**
     * 新增数据执行
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        if (!ObjectUtils.isEmpty(mybatisMateProperties.getInsertMetaObject())) {
            mybatisMateProperties.getInsertMetaObject().forEach(l -> {
                System.out.println("------------开始填充指定插入值---------------");
                this.strictInsertFill(metaObject, l.getFieldName(), l.getFieldType(), l.getFieldVal());
                logger.info("-----------字段名:{}---------字段类型:{}------------字段填充值:{}",l.getFieldName(),l.getFieldType(),l.getFieldVal());

            });
        } else {
            System.out.println("------------开始填充默认插入值---------------");
            this.strictInsertFill(metaObject, "createdDate", LocalDateTime.class, LocalDateTime.now());
            this.strictInsertFill(metaObject, "updatedDate", LocalDateTime.class, LocalDateTime.now());
            this.strictInsertFill(metaObject, "deleteFlag", Integer.class, 0);
        }
    }

    /**
     * 更新数据执行
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "updatedDate", LocalDateTime.class, LocalDateTime.now());
        if (!ObjectUtils.isEmpty(mybatisMateProperties.getUpdateMetaObject())) {
            mybatisMateProperties.getInsertMetaObject().forEach(l -> {
                System.out.println("------------开始填充指定更新值---------------");
                this.strictInsertFill(metaObject, l.getFieldName(), l.getFieldType(), l.getFieldVal());
                logger.info("-----------字段名:{}---------字段类型:{}------------字段填充值:{}",l.getFieldName(),l.getFieldType(),l.getFieldVal());

            });
        } else {
            System.out.println("------------开始填充默认更新值---------------");
            this.strictInsertFill(metaObject, "updatedDate", LocalDateTime.class, LocalDateTime.now());

        }
    }
}
