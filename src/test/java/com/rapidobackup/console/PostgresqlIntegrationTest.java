package com.rapidobackup.console;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration test to verify PostgreSQL configuration with Testcontainers.
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public class PostgresqlIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    static {
        postgres.start();
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
        System.setProperty("spring.datasource.driver-class-name", postgres.getDriverClassName());
    }

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
        assertThat(postgres.isRunning()).isTrue();
        assertThat(postgres.getDatabaseName()).isEqualTo("testdb");
    }
}