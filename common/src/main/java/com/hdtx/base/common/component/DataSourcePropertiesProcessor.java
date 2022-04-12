package com.hdtx.base.common.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.context.properties.ConfigurationBeanFactoryMetadata;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindHandlerAdvisor;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.bind.handler.IgnoreTopLevelConverterNotFoundBindHandler;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.PropertySources;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 *
 * refer to ConfigurationPropertiesBindingPostProcessor and
 * ConfigurationPropertiesBinder
 * @Author liubin
 * @Date 2017/7/14 10:11
 */
@Component
public class DataSourcePropertiesProcessor implements ApplicationContextAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DataSourcePropertiesProcessor.class);

    private ConfigurationBeanFactoryMetadata beanFactoryMetadata;

    private ApplicationContext applicationContext;

    private PropertySources propertySources;

    private Validator configurationPropertiesValidator;

    private volatile Validator jsr303Validator;

    private volatile Binder binder;

    private ConversionService conversionService;

    private DefaultConversionService defaultConversionService;

    private List<Converter<?, ?>> converters = Collections.emptyList();

    private List<GenericConverter> genericConverters = Collections.emptyList();

    private boolean jsr303Present;

    public void postProcessBeforeInitialization(Object bean, String beanName, String prefix) {
        ResolvableType type = getBeanType(bean, beanName);
        Validated validated = getAnnotation(bean, beanName, Validated.class);
        Annotation[] annotations = (validated != null)
                ? new Annotation[] {validated }
                : new Annotation[] {};
        Bindable<?> target = Bindable.of(type).withExistingValue(bean)
                .withAnnotations(annotations);
        try {
            this.bind(prefix, target);
        }
        catch (Exception ex) {
            throw new BeanCreationException(
                    String.format("Could not bind properties to class: %s, beanName: %s, prefix: %s",
                            bean.getClass(), beanName, prefix), ex);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.beanFactoryMetadata = this.applicationContext.getBean(
                ConfigurationBeanFactoryMetadata.BEAN_NAME,
                ConfigurationBeanFactoryMetadata.class);

        this.propertySources = new PropertySourcesDeducer(applicationContext)
                .getPropertySources();
        this.configurationPropertiesValidator = getConfigurationPropertiesValidator(
                applicationContext, ConfigurationPropertiesBindingPostProcessor.VALIDATOR_BEAN_NAME);

        this.jsr303Present = ConfigurationPropertiesJsr303Validator
                .isJsr303Present(applicationContext);

        if (this.conversionService == null) {
            this.conversionService = getOptionalConversionService();
        }
    }

    public void bind(String prefix, Bindable<?> target) {
        List<Validator> validators = getValidators(target);
        BindHandler bindHandler = getBindHandler(validators);
        getBinder().bind(prefix, target, bindHandler);
    }

    private ResolvableType getBeanType(Object bean, String beanName) {
        Method factoryMethod = this.beanFactoryMetadata.findFactoryMethod(beanName);
        if (factoryMethod != null) {
            return ResolvableType.forMethodReturnType(factoryMethod);
        }
        return ResolvableType.forClass(bean.getClass());
    }

    private <A extends Annotation> A getAnnotation(Object bean, String beanName,
                                                   Class<A> type) {
        A annotation = this.beanFactoryMetadata.findFactoryAnnotation(beanName, type);
        if (annotation == null) {
            annotation = AnnotationUtils.findAnnotation(bean.getClass(), type);
        }
        return annotation;
    }

    /** ConfigurationPropertiesBinder start **/

    private BindHandler getBindHandler(List<Validator> validators) {
        BindHandler handler = new IgnoreTopLevelConverterNotFoundBindHandler();
        if (!validators.isEmpty()) {
            handler = new ValidationBindHandler(handler,
                    validators.toArray(new Validator[0]));
        }
        for (ConfigurationPropertiesBindHandlerAdvisor advisor : getBindHandlerAdvisors()) {
            handler = advisor.apply(handler);
        }
        return handler;
    }

    private List<ConfigurationPropertiesBindHandlerAdvisor> getBindHandlerAdvisors() {
        return this.applicationContext
                .getBeanProvider(ConfigurationPropertiesBindHandlerAdvisor.class)
                .orderedStream().collect(Collectors.toList());
    }

    private ConversionService getOptionalConversionService() {
        try {
            return this.applicationContext.getBean(
                    ConfigurableApplicationContext.CONVERSION_SERVICE_BEAN_NAME,
                    ConversionService.class);
        }
        catch (NoSuchBeanDefinitionException ex) {
            return null;
        }
    }



    private List<Validator> getValidators(Bindable<?> target) {
        List<Validator> validators = new ArrayList<>(3);
        if (this.configurationPropertiesValidator != null) {
            validators.add(this.configurationPropertiesValidator);
        }
        if (this.jsr303Present && target.getAnnotation(Validated.class) != null) {
            validators.add(getJsr303Validator());
        }
        if (target.getValue() != null && target.getValue().get() instanceof Validator) {
            validators.add((Validator) target.getValue().get());
        }
        return validators;
    }

    private Validator getJsr303Validator() {
        if (this.jsr303Validator == null) {
            this.jsr303Validator = new ConfigurationPropertiesJsr303Validator(
                    this.applicationContext);
        }
        return this.jsr303Validator;
    }



    private Binder getBinder() {
        if (this.binder == null) {
            this.binder = new Binder(getConfigurationPropertySources(),
                    getPropertySourcesPlaceholdersResolver(),
                    this.conversionService == null ? getDefaultConversionService() : this.conversionService,
                    getPropertyEditorInitializer());
        }
        return this.binder;
    }

    private Iterable<ConfigurationPropertySource> getConfigurationPropertySources() {
        return ConfigurationPropertySources.from(this.propertySources);
    }

    private PropertySourcesPlaceholdersResolver getPropertySourcesPlaceholdersResolver() {
        return new PropertySourcesPlaceholdersResolver(this.propertySources);
    }


    private Consumer<PropertyEditorRegistry> getPropertyEditorInitializer() {
        if (this.applicationContext instanceof ConfigurableApplicationContext) {
            return ((ConfigurableApplicationContext) this.applicationContext)
                    .getBeanFactory()::copyRegisteredEditorsTo;
        }
        return null;
    }

    private ConversionService getDefaultConversionService() {
        if (this.defaultConversionService == null) {
            DefaultConversionService conversionService = new DefaultConversionService();
            this.applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
            for (Converter<?, ?> converter : this.converters) {
                conversionService.addConverter(converter);
            }
            for (GenericConverter genericConverter : this.genericConverters) {
                conversionService.addConverter(genericConverter);
            }
            this.defaultConversionService = conversionService;
        }
        return this.defaultConversionService;
    }

    private Validator getConfigurationPropertiesValidator(
            ApplicationContext applicationContext, String validatorBeanName) {
        if (applicationContext.containsBean(validatorBeanName)) {
            return applicationContext.getBean(validatorBeanName, Validator.class);
        }
        return null;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
