package com.rapidobackup.console.agent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.rapidobackup.console.agent.repository"
)
public class AgentJpaConfig {

    // @Value("${spring.datasource.url}")
    // private String dbUrl;

    // @Value("${spring.datasource.username}")
    // private String dbUsername;

    // @Value("${spring.datasource.password}")
    // private String dbPassword;

    // @Bean
    // public DataSource dataSource() {
    //     return DataSourceBuilder.create()
    //             .url(dbUrl)
    //             .username(dbUsername)
    //             .password(dbPassword)
    //             .build();
    // }

    // @Bean
    // public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    //     LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    //     em.setDataSource(dataSource);
    //     em.setPackagesToScan("com.rapidobackup.console.agent.entity");
    //     // Additional JPA properties can be set here
    //     return em;
    // }

    // @Bean
    // public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
    //     return new JpaTransactionManager(emf);
    // }
}
