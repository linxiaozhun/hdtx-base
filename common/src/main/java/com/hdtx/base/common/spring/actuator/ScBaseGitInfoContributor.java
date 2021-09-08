package com.hdtx.base.common.spring.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.info.GitProperties;

public class ScBaseGitInfoContributor extends CustomGitInfoContributor {

    public ScBaseGitInfoContributor(GitProperties properties) {
        super(properties);
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("hdtx-base", generateContent());
    }
}
