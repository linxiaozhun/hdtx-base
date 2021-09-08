package com.hdtx.base.common.utils;

import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author: guomouren
 * @date: 2021/8/23 10:58
 * @description:
 */
@Slf4j
public class ExcelInputUtil {
    /**
     * @method: ExcelRead
     * @Description: excel读取，返回list
     * @param file excel文件
     * @param c 导入模板类
     * @Author: gxg
     * @Date: 2021/8/23
     **/
    public static<T> List<T> ExcelRead(MultipartFile file,Class<?> c){
        try {
            List list= EasyExcel.read(file.getInputStream()).head(c).sheet().doReadSync();
            return list;
        }catch (Exception e){
            log.error("excel表解析异常：",e);
            return null;
        }
    }
}
