package com.hdtx.base.common.spring.mybatisplus.generator;

import com.baomidou.mybatisplus.generator.config.rules.DateType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mybatis-plus.generator")
@Getter
@Setter
public class MybatisPlusGeneratorProperties {

    /**
     * 项目根目录地址
     */
    private String prentPath;

    /**
     * service 地址
     */
    private String servicePath;

    private String entityPath;

    private String controllerPath;

    /**
     * mapper层地址
     */
    private String mapperPath;

    /**
     * mapper.xml文件存放地址
     */
    private String mapperXmlPath;

    /**
     * 文件输出路径
     */
    private String outputDir;

    private String dataSourceUrl;

    private String userName;

    private String password;

    private String authorName="sunnyKaKa";

    private DateType dateType;

    private String dateFormatPattern="yyyy-MM-dd HH:mm:ss";

    private boolean isOpenDir=true;

    /**
     * 表名称多个用“,”号隔开
     */
    private String tableName="sys_user";

    /**
     * 表前缀排除 例如：t_,sys_ 多个用逗号隔开
     */
    private String tablePrefix;

    /**
     * 排除的列名 多个用逗号隔开
     */
    private String ignoreColumn;


    private String schemaName;




}
