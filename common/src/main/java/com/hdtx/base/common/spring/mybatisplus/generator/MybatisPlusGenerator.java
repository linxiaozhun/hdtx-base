package com.hdtx.base.common.spring.mybatisplus.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.SimpleAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.querys.KingbaseESQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.SneakyThrows;

import java.io.File;



public class MybatisPlusGenerator extends SimpleAutoGenerator {


    private MybatisPlusGeneratorProperties mybatisPlusGeneratorProperties;

    public MybatisPlusGenerator(MybatisPlusGeneratorProperties mybatisPlusGeneratorProperties) {
        this.mybatisPlusGeneratorProperties = mybatisPlusGeneratorProperties;
    }

    @Override
    public IConfigBuilder<DataSourceConfig> dataSourceConfigBuilder() {
        return new DataSourceConfig.Builder(mybatisPlusGeneratorProperties.getDataSourceUrl()
        ,mybatisPlusGeneratorProperties.getUserName(),mybatisPlusGeneratorProperties.getPassword())
                .schema(mybatisPlusGeneratorProperties.getSchemaName());
    }

    public IConfigBuilder<PackageConfig> packageConfigBuilder() {
        return (new com.baomidou.mybatisplus.generator.config.PackageConfig.Builder()).parent(mybatisPlusGeneratorProperties.getPrentPath())
                .controller(mybatisPlusGeneratorProperties.getControllerPath())
                .serviceImpl(mybatisPlusGeneratorProperties.getServicePath())
                .entity(mybatisPlusGeneratorProperties.getEntityPath())
                .mapper(mybatisPlusGeneratorProperties.getMapperPath())
                .xml(mybatisPlusGeneratorProperties.getMapperXmlPath())
                ;
    }

    public IConfigBuilder<GlobalConfig> globalConfigBuilder() {
        String outputDir = new File(System.getProperty("user.dir")) + mybatisPlusGeneratorProperties.getOutputDir();
        System.out.println("\n输出文件目录：" + outputDir);
        return (new GlobalConfig.Builder()).fileOverride().enableSwagger()
                .openDir(mybatisPlusGeneratorProperties.isOpenDir()).outputDir(outputDir)
                .author(mybatisPlusGeneratorProperties.getAuthorName())
                .dateType(mybatisPlusGeneratorProperties.getDateType())
                .commentDate(mybatisPlusGeneratorProperties.getDateFormatPattern())
                ;

    }

    public IConfigBuilder<StrategyConfig> strategyConfigBuilder() {
        return (new com.baomidou.mybatisplus.generator.config.StrategyConfig.Builder()).
                addInclude(mybatisPlusGeneratorProperties.getTableName().split(","))
                .addTablePrefix(mybatisPlusGeneratorProperties.getTablePrefix().split(","))
                .enableCapitalMode().mapperBuilder()
                .serviceBuilder()
                .mapperBuilder()
                .controllerBuilder()
                .entityBuilder().naming(NamingStrategy.underline_to_camel)
                .columnNaming(NamingStrategy.underline_to_camel)
                .enableLombok()
                .idType(IdType.AUTO)
                .addIgnoreColumns(mybatisPlusGeneratorProperties.getIgnoreColumn().split(","))
                ;
    }

    public AbstractTemplateEngine templateEngine() {
        return new FreemarkerTemplateEngine();
    }

    public IConfigBuilder<TemplateConfig> templateConfigBuilder() {
        return new TemplateConfig.Builder();
    }


}
