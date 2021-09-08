package com.hdtx.base.utils;

/**
 * @Author liubin
 * @Date 2017/5/15 10:46
 */
public interface UpdateByIdFunction {

    int execute(Long[] ids, Object... args);

}
