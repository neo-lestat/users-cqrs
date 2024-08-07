package org.example.usersread.infrastructure.db.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "org.example.usersread.infrastructure.db.repository")
@ConfigurationProperties("spring.datasource")
@EnableJpaAuditing
@EntityScan(basePackages = "org.example.usersread.infrastructure.db.entity")
public class SpringDataConfig {

}
