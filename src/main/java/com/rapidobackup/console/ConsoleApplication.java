package com.rapidobackup.console;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class supporting both JPA (traditional modules) 
 * and R2DBC (Agent module) for hybrid reactive/blocking architecture
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableTransactionManagement
public class ConsoleApplication {

  public static void main(String[] args) {
    SpringApplication.run(ConsoleApplication.class, args);
  }
}