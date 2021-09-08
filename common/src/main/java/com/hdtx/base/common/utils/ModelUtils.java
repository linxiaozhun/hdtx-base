package com.hdtx.base.common.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: ghx
 * @date 2021/8/13
 * @describe:
 */
@Slf4j
public class ModelUtils {
    /**
     * 检查实体类对应的数据库中是否存在fileName字段
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static boolean checkField(Class<?> clazz, String fieldName) {
        if (StrUtil.isEmpty(fieldName)) {
            log.error("fieldName is empty!!!");
            return false;
        }
        List<String> fieldNameList = new ArrayList<>();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        for (Field field : fieldList) {
            field.setAccessible(true);
            fieldNameList.add(field.getName());
        }
        return fieldNameList.contains(fieldName);
    }

    /** 根据指定字段获取对象中的值
     * @param o
    	 * @param fieldName
     [o, fieldName]* @return java.lang.String
     * @author xiaoLin
     * @creed: Talk is cheap,show me the code
     * @date 2021/8/19 0019 18:00
     */
    public static Object getObjectValue(Object o, String fieldName) {
        if (StrUtil.isEmpty(fieldName)) {
            log.error("fieldName is empty!!!");
            return null;
        }
        Class<?> c = o.getClass();
        try {
            List<Field> fieldList = new ArrayList<>();
            while (c != null) {
                fieldList.addAll(new ArrayList<>(Arrays.asList(c.getDeclaredFields())));
                c = c.getSuperclass();
            }
            for (Field field : fieldList) {
                field.setAccessible(true);
                if (field.getName().equals(fieldName)) {
                    return  field.get(o);
                }
            }
        } catch (IllegalAccessException i) {
            log.error("类实列化失败:", i);
        }
        return null;
    }


    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 驼峰转下划线
     */
    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 下划线转驼峰
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
