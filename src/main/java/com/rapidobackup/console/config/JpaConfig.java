package com.rapidobackup.console.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration for JPA repositories
 * R2DBC repositories are handled separately in R2dbcConfig
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "com.rapidobackup.console",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX, 
        pattern = "com.rapidobackup.console.agent.repository.AgentRepository"
    )
)
@EnableJpaAuditing
public class JpaConfig {
    // Includes all JPA repositories except R2DBC AgentRepository
    // @EnableTransactionManagement is in ConsoleApplication
}