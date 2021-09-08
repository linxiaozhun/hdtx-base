package com.hdtx.base.common.spring;

import com.hdtx.base.common.spring.actuator.*;
import com.hdtx.base.common.spring.container.CustomConfigurableContainer;

import com.hdtx.base.common.spring.mvc.AppExceptionHandlerController;
import com.hdtx.base.common.spring.mvc.BaseWebMvcConfig;
import com.hdtx.base.common.spring.security.SpringSecurityBaseConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.info.GitProperties;
import org.springframework.boot.info.InfoProperties;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @Author liubin
 * @Date 2017/5/15 15:04
 */
@EnableSwagger2
@Import({ScDataSourcesHealthIndicatorConfiguration.class, ScRedisHealthIndicatorConfiguration.class})
public class WebApplication {

    private static final Logger logger = LoggerFactory.getLogger(WebApplication.class);


    /**
     * The default order for the core {@link InfoContributor} beans.
     */
    public static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 20;


    @Bean
    public WebServerFactoryCustomizer webServerFactoryCustomizer() {
        return new CustomConfigurableContainer();
    }

    //exception handler
    @Bean
    public AppExceptionHandlerController appExceptionHandlerController() {
        return new AppExceptionHandlerController();
    }

    @Bean
    public Docket api(ApplicationConstant applicationConstant) {

        if(applicationConstant.webProject && applicationConstant.isProdProfile()) {
            return new Docket(DocumentationType.SWAGGER_2).enable(false);
        } else {
            return new Docket(DocumentationType.SWAGGER_2)
                    .groupName("hdtx-base")
                    .directModelSubstitute(LocalDate.class, java.sql.Date.class)
                    .directModelSubstitute(LocalDateTime.class, java.util.Date.class)
                    .select()
                    .apis(input -> {
                        String packageName = input.declaringClass().getPackage().getName();
                        return (packageName.startsWith("com.hdtx.")) &&
                                (packageName.contains(".web") || packageName.contains(".controller"));
                    })
                    .paths(PathSelectors.any())
                    .build();
        }
    }




    @Bean
    public WebMvcConfigurer baseWebMvcConfigurer() {
        return new BaseWebMvcConfig();
    }



    @Bean
    @Order(DEFAULT_ORDER)
    public CustomGitInfoContributor gitInfoContributor(GitProperties properties) {
        return new CustomGitInfoContributor(properties);
    }

    @Bean
    @Order(DEFAULT_ORDER)
    public ApplicationInfoContributor applicationInfoContributor(ApplicationContext applicationContext) {

        Properties properties = new Properties();
        try {
            ApplicationConstant constant = applicationContext.getBean(ApplicationConstant.class);
            properties.put("name", constant.applicationName);
            properties.put("profile", constant.profile);
            String configName = applicationContext.getEnvironment().getProperty("spring.cloud.config.name");
            if(StringUtils.isBlank(configName)) {
                configName = constant.applicationName;
            }
            properties.put("config.name", configName);

        } catch (Exception e) {
            logger.error("", e);
        }

        return new ApplicationInfoContributor(new InfoProperties(properties));
    }


    @Bean
    @Order(DEFAULT_ORDER)
    public ScBaseGitInfoContributor tdBaseInfoContributor() {

        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources("git.properties");
            int i = 0;
            URL resource = resources.nextElement();
            Resource tdBaseGitResource = null;
            do {
                if(i != 0) {
                    resource = resources.nextElement();
                }
                if(resource.toString().contains("/common-1.0")) {
                    tdBaseGitResource = new UrlResource(resource);
                    break;
                }
                i++;
            } while (resources.hasMoreElements());

            if(tdBaseGitResource != null) {
                GitProperties tdBaseProperties = new GitProperties(loadFrom(tdBaseGitResource, "git"));
                return new ScBaseGitInfoContributor(tdBaseProperties);
            }

        } catch (Exception e) {
            logger.warn("获取sc-base info发生错误", e);
        }

        return new ScBaseGitInfoContributor(new GitProperties(new Properties()));

    }

    protected Properties loadFrom(Resource location, String prefix) throws IOException {
        String p = prefix.endsWith(".") ? prefix : prefix + ".";
        Properties source = PropertiesLoaderUtils.loadProperties(location);
        Properties target = new Properties();
        for (String key : source.stringPropertyNames()) {
            if (key.startsWith(p)) {
                target.put(key.substring(p.length()), source.get(key));
            }
        }
        return target;
    }




    @Bean
    @ConditionalOnMissingBean
    public SpringSecurityBaseConfiguration springSecurityBaseConfiguration() {
        return new SpringSecurityBaseConfiguration();
    }

}