package com.hdtx.base.common.spring.actuator;

import org.springframework.boot.actuate.info.GitInfoContributor;
import org.springframework.boot.info.GitProperties;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Properties;

public class CustomGitInfoContributor extends GitInfoContributor {

    public CustomGitInfoContributor(GitProperties properties) {
        super(properties);
    }

    @Override
    protected PropertySource<?> toSimplePropertySource() {
        Properties props = new Properties();
        copyIfSet(props, "branch");
        String commitId = getProperties().getShortCommitId();
        if (commitId != null) {
            props.put("commit.id", commitId);
        }
        copyIfSet(props, "build.version");
        copyIfSet(props, "commit.time");

        return new PropertiesPropertySource("git", props);

    }




}
