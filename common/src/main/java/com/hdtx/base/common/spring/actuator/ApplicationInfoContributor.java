package com.hdtx.base.common.spring.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoPropertiesInfoContributor;
import org.springframework.boot.info.InfoProperties;
import org.springframework.core.env.PropertySource;

public class ApplicationInfoContributor extends InfoPropertiesInfoContributor<InfoProperties> {

    public ApplicationInfoContributor(InfoProperties infoProperties) {
        super(infoProperties, Mode.FULL);
    }

    @Override
    protected PropertySource<?> toSimplePropertySource() {
        return getProperties().toPropertySource();
    }


    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("application", generateContent());
    }
}
