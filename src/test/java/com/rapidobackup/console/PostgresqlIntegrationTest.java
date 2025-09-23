package com.rapidobackup.console;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration test to verify PostgreSQL configuration with Testcontainers.
 */
public class PostgresqlIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void contextLoads() {
        // Test that the Spring context loads successfully with PostgreSQL
        assertThat(dataSource).isNotNull();
    }

    @Test
    void postgresqlContainerIsRunning() {
        // Test that the PostgreSQL container is running
        assertThat(postgresContainer.isRunning()).isTrue();
        assertThat(postgresContainer.getDatabaseName()).isEqualTo("testdb");
    }
}