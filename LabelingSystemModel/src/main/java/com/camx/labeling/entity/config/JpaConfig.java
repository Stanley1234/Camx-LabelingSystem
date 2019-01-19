package com.camx.labeling.entity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.camx.labeling.entity")
public class JpaConfig {
}
