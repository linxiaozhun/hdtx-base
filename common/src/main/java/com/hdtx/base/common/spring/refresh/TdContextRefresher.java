package com.hdtx.base.common.spring.refresh;

import com.hdtx.base.common.spring.TdBaseApplicationContextInitializer;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.cloud.bootstrap.BootstrapApplicationListener;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.*;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.util.*;

/**
 * Created by liubin on 2017/11/29.
 */
public class TdContextRefresher extends ContextRefresher {

    private static final String REFRESH_ARGS_PROPERTY_SOURCE = "refreshArgs";

    private static final String[] DEFAULT_PROPERTY_SOURCES = new String[]{
            // order matters, if cli args aren't first, things get messy
            CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME,
            "defaultProperties"};

    private Set<String> standardSources = new HashSet<>(
            Arrays.asList(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME,
                    StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                    StandardServletEnvironment.JNDI_PROPERTY_SOURCE_NAME,
                    StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME,
                    StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME,
                    "configurationProperties"));

    private ConfigurableApplicationContext context;

    private RefreshScope scope;

    public TdContextRefresher(ConfigurableApplicationContext context, RefreshScope scope) {
        super(context, scope);
        this.context = context;
        this.scope = scope;
    }


    public synchronized Set<String> refreshEnvironment() {
        Map<String, Object> before = extract(
                this.context.getEnvironment().getPropertySources());
        addConfigFilesToEnvironment();
        Set<String> keys = changes(before,
                extract(this.context.getEnvironment().getPropertySources())).keySet();
        //不发送EnvironmentChangeEvent事件, 避免ConfigurationPropertiesRebinder.rebind方法调用
//		this.context.publishEvent(new EnvironmentChangeEvent(this.context, keys));
        return keys;
    }


    /* For testing. */ ConfigurableApplicationContext addConfigFilesToEnvironment() {
        ConfigurableApplicationContext capture = null;
        try {
            StandardEnvironment environment = copyEnvironment(
                    this.context.getEnvironment());
            SpringApplicationBuilder builder = new SpringApplicationBuilder(Empty.class)
                    .bannerMode(Mode.OFF).web(WebApplicationType.NONE)
                    // 修改, 标记context为临时的
                    .properties(TdBaseApplicationContextInitializer.TEMP_CONTEXT_NAME + "=true")
                    .environment(environment);
            // Just the listeners that affect the environment (e.g. excluding logging
            // listener because it has side effects)
            builder.application()
                    .setListeners(Arrays.asList(new BootstrapApplicationListener(),
                            new ConfigFileApplicationListener()));
            capture = builder.run();
            if (environment.getPropertySources().contains(REFRESH_ARGS_PROPERTY_SOURCE)) {
                environment.getPropertySources().remove(REFRESH_ARGS_PROPERTY_SOURCE);
            }
            MutablePropertySources target = this.context.getEnvironment()
                    .getPropertySources();
            String targetName = null;
            for (PropertySource<?> source : environment.getPropertySources()) {
                String name = source.getName();
                if (target.contains(name)) {
                    targetName = name;
                }
                if (!this.standardSources.contains(name)) {
                    if (target.contains(name)) {
                        target.replace(name, source);
                    } else {
                        if (targetName != null) {
                            target.addAfter(targetName, source);
                        } else {
                            // targetName was null so we are at the start of the list
                            target.addFirst(source);
                            targetName = name;
                        }
                    }
                }
            }
        } finally {
            ConfigurableApplicationContext closeable = capture;
            while (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    // Ignore;
                }
                if (closeable.getParent() instanceof ConfigurableApplicationContext) {
                    closeable = (ConfigurableApplicationContext) closeable.getParent();
                } else {
                    break;
                }
            }
        }
        return capture;
    }


    // Don't use ConfigurableEnvironment.merge() in case there are clashes with property
    // source names
    private StandardEnvironment copyEnvironment(ConfigurableEnvironment input) {
        StandardEnvironment environment = new StandardEnvironment();
        MutablePropertySources capturedPropertySources = environment.getPropertySources();
        // Only copy the default property source(s) and the profiles over from the main
        // environment (everything else should be pristine, just like it was on startup).
        for (String name : DEFAULT_PROPERTY_SOURCES) {
            if (input.getPropertySources().contains(name)) {
                if (capturedPropertySources.contains(name)) {
                    capturedPropertySources.replace(name,
                            input.getPropertySources().get(name));
                } else {
                    capturedPropertySources.addLast(input.getPropertySources().get(name));
                }
            }
        }
        environment.setActiveProfiles(input.getActiveProfiles());
        environment.setDefaultProfiles(input.getDefaultProfiles());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("spring.jmx.enabled", false);
        map.put("spring.main.sources", "");
        capturedPropertySources
                .addFirst(new MapPropertySource(REFRESH_ARGS_PROPERTY_SOURCE, map));
        return environment;
    }

    private Map<String, Object> changes(Map<String, Object> before,
                                        Map<String, Object> after) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (String key : before.keySet()) {
            if (!after.containsKey(key)) {
                result.put(key, null);
            } else if (!equal(before.get(key), after.get(key))) {
                result.put(key, after.get(key));
            }
        }
        for (String key : after.keySet()) {
            if (!before.containsKey(key)) {
                result.put(key, after.get(key));
            }
        }
        return result;
    }

    private boolean equal(Object one, Object two) {
        if (one == null && two == null) {
            return true;
        }
        if (one == null || two == null) {
            return false;
        }
        return one.equals(two);
    }

    private Map<String, Object> extract(MutablePropertySources propertySources) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<PropertySource<?>> sources = new ArrayList<PropertySource<?>>();
        for (PropertySource<?> source : propertySources) {
            sources.add(0, source);
        }
        for (PropertySource<?> source : sources) {
            if (!this.standardSources.contains(source.getName())) {
                extract(source, result);
            }
        }
        return result;
    }

    private void extract(PropertySource<?> parent, Map<String, Object> result) {
        if (parent instanceof CompositePropertySource) {
            try {
                List<PropertySource<?>> sources = new ArrayList<PropertySource<?>>();
                for (PropertySource<?> source : ((CompositePropertySource) parent)
                        .getPropertySources()) {
                    sources.add(0, source);
                }
                for (PropertySource<?> source : sources) {
                    extract(source, result);
                }
            } catch (Exception e) {
                return;
            }
        } else if (parent instanceof EnumerablePropertySource) {
            for (String key : ((EnumerablePropertySource<?>) parent).getPropertyNames()) {
                result.put(key, parent.getProperty(key));
            }
        }
    }


//	@Override
//	public synchronized Set<String> refresh() {
//		Map<String, Object> before = extract(
//				this.context.getEnvironment().getPropertySources());
//		addConfigFilesToEnvironment();
//		Set<String> keys = changes(before,
//				extract(this.context.getEnvironment().getPropertySources())).keySet();
//		//不发送EnvironmentChangeEvent事件, 避免ConfigurationPropertiesRebinder.rebind方法调用
////		this.context.publishEvent(new EnvironmentChangeEvent(keys));
//		keys.remove(TEMP_CONTEXT_NAME);
//		this.scope.refreshAll();
//		return keys;
//	}


}
