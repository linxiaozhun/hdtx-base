package com.hdtx.base.common.spring.mybatis;

import com.baomidou.mybatisplus.core.handlers.StrictFill;
import com.hdtx.base.common.exception.AppBusinessException;
import com.hdtx.base.common.utils.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@ConfigurationProperties("mybatis-meta")
@Getter
@Setter
@Slf4j
public class MybatisMateProperties {


    private List<MetaObject> insertMetaObject;

    private List<MetaObject> updateMetaObject;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MetaObject {
        private String fieldName;

        private JavaType fieldType;

        private String fieldValue;
    }

    public enum JavaType {
        STRING("java.lang.String"), INT("java.lang.Integer"), LONG("java.lang.Long"), LOCAL_DATE_TIME("java.time.LocalDateTime");

        public String classPath;

        public String getClassPath() {
            return classPath;
        }

        JavaType(String classPath) {
            this.classPath = classPath;
        }

        public static Class forClass(String javaTypeName) {
            JavaType javaType = JavaType.valueOf(javaTypeName);
            for (JavaType value : JavaType.values()) {
                if (value.name().equalsIgnoreCase(javaTypeName)) {
                    try {
                        return ClassUtils.forName(javaType.getClassPath(), ClassUtils.getDefaultClassLoader());
                    } catch (ClassNotFoundException c) {
                        throw new RuntimeException("java基础类型转换失败");
                    }
                }
            }
            return null;
        }

        public static Supplier forClassByValue(String javaTypeName, String fieldValue) {

            JavaType javaType = JavaType.valueOf(javaTypeName);
            switch (javaType) {
                case STRING:
                    return () -> String.valueOf(fieldValue);
                case INT:
                    return () -> Integer.valueOf(fieldValue);

                case LONG:
                    return () -> Long.valueOf(fieldValue);

                case LOCAL_DATE_TIME:
                    return () -> DateTimeUtils.parse(fieldValue);

                default:
                    throw new AppBusinessException("未知的转换类型");
            }

        }


    }

    public static List<StrictFill> ofStrictFill(List<MetaObject> metaObjects) {
        List<StrictFill> strictFillList = new ArrayList<>();
        if (!metaObjects.isEmpty()) {
            metaObjects.forEach(m -> {
                StrictFill strictFill = new StrictFill(m.getFieldName(), JavaType.forClass(m.fieldType.name()), JavaType.forClassByValue(m.fieldType.name(), m.fieldValue));
                strictFillList.add(strictFill);
            });
        }
        return strictFillList;
    }


}
