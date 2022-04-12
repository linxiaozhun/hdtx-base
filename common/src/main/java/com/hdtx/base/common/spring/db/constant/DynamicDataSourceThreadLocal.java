package com.hdtx.base.common.spring.db.constant;

/**
 * @author XiaoLin
 * @date 2021年12月10日
 * @time 10:37
 */
public class DynamicDataSourceThreadLocal {

    /**
     * 将当前指定数据源存与等地线程内,做动态隔离
     */
    private static volatile  ThreadLocal<String> dataSourceThreadLocal = ThreadLocal.withInitial(String::new);


    public static void setDataSourceKey(String dataSourceKey){
        dataSourceThreadLocal.set(dataSourceKey);
    }

    public static String getDataSourceKey() {
        return dataSourceThreadLocal.get();
    }

    public static void clearDataSource(){
        dataSourceThreadLocal.remove();;
    }

}
