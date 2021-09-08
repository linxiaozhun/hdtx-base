package com.hdtx.base.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
public class FileNameUtils {
    /**m
     *
     * 处理下载中文文件名
     *
     * @param request
     * @param fileName 需要中文处理的字段
     * @return
     */
    public static String URLEncoder(HttpServletRequest request,String fileName){
        try{
            if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE")>0){
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {
                fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            }
        }catch (UnsupportedEncodingException e){
                log.error("文件名称转换异常",e);
        }
        return fileName;
    }
}
