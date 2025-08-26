package com.rapidobackup.console.agent.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;

/**
 * Configuration for R2DBC alongside JPA to enable reactive database access
 * for the Agent module while maintaining JPA for other modules
 */
@Configuration
@EnableR2dbcRepositories(basePackages = "com.rapidobackup.console.agent.repository")
@EnableR2dbcAuditing
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    /**
     * Configures reactive transaction manager for R2DBC operations
     * This is separate from the JPA transaction manager
     */
    @Bean("reactiveTransactionManager")
    public ReactiveTransactionManager reactiveTransactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }
}