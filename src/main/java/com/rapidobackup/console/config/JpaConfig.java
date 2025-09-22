package com.rapidobackup.console.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;

/**
 * JPA configuration alongside R2DBC using HikariCP DataSource
 * Explicitly configures HikariCP with auto-commit: false
 */
@Configuration
@EnableJpaRepositories(basePackages = {
    "com.rapidobackup.console.user.repository",
    "com.rapidobackup.console.auth.repository",
    "com.rapidobackup.console.backup.repository",
    "com.rapidobackup.console.tenant.repository"
}, entityManagerFactoryRef = "entityManagerFactory",
   transactionManagerRef = "transactionManager")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class JpaConfig {

    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) {
        
        // Create HikariCP DataSource with explicit auto-commit configuration
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver");
        
        // Explicitly set auto-commit to false - this fixes the autocommit issue
        dataSource.setAutoCommit(false);
        dataSource.setPoolName("Hikari");
        
        return dataSource;
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("dataSource") DataSource dataSource) {
        
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.rapidobackup.console");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        
        return factory;
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}