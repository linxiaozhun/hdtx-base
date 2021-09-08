package com.hdtx.base.common.spring.actuator.dto;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceResourceDTO {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceResourceDTO.class);

    private String url;

    private String host;

    private int port;

    private String type;

    private String username;

    private Boolean readonly;

    //git配置中配置的前缀key
    private String dataSourceKey;

    private String beanId;

    public static DataSourceResourceDTO fromDruidDataSource(String dataSourceKey, DruidDataSource druidDataSource) {

        try {

            DataSourceResourceDTO dto = new DataSourceResourceDTO();
            dto.setDataSourceKey(dataSourceKey);
            dto.setBeanId(dataSourceKey);
            dto.setUrl(druidDataSource.getRawJdbcUrl());
            dto.setUsername(druidDataSource.getUsername());



            Boolean readonly = null;
            if(dataSourceKey.startsWith("config.db.")) {
                if(dataSourceKey.contains("write")) {
                    readonly = false;
                } else if(dataSourceKey.contains("read")) {
                    readonly = true;
                }
            } else if(dataSourceKey.startsWith("db.ds.write")) {
                readonly = false;
            } else if(dataSourceKey.startsWith("db.ds.read")) {
                readonly = true;
            }

            dto.setReadonly(readonly);

            return dto;

        } catch (NoClassDefFoundError ignore) {

            return null;
        } catch (Throwable e) {

            logger.error("", e);
            return null;
        }

    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getReadonly() {
        return readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    public String getDataSourceKey() {
        return dataSourceKey;
    }

    public void setDataSourceKey(String dataSourceKey) {
        this.dataSourceKey = dataSourceKey;
    }
}
