package com.hdtx.base.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Author liubin
 * @Date 2017/10/27 18:34
 */
public abstract class DateTimeUtils {

    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date toDate(LocalDateTime dateTime) {
        if(dateTime == null) return null;
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String format(LocalDateTime dateTime, String format){
        if(dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern(format));
    }

    public static LocalDateTime parse(String dateTimeStr, String format){
        if(dateTimeStr == null) return null;
        DateTimeFormatter sf = DateTimeFormatter.ofPattern(format);
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, sf);
        return dateTime;
    }

    public static String format(LocalDateTime dateTime){
        return format(dateTime, DEFAULT_DATE_TIME_FORMAT);
    }

    public static LocalDateTime parse(String dateTimeStr){
        return parse(dateTimeStr, DEFAULT_DATE_TIME_FORMAT);
    }



}
