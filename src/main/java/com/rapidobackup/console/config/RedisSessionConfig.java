package com.rapidobackup.console.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Redis session configuration that is conditionally enabled based on Spring profiles.
 *
 * This configuration is active when:
 * - NOT in 'dev' profile (i.e., in production, test, etc.)
 * - OR when 'dev-redis' profile is explicitly activated
 *
 * This allows developers to optionally use Redis in development by activating
 * both 'dev' and 'dev-redis' profiles together.
 *
 * Usage examples:
 * - Development without Redis (default): -Dspring.profiles.active=dev
 * - Development with Redis: -Dspring.profiles.active=dev,dev-redis
 * - Production: -Dspring.profiles.active=prod
 */
@Configuration
@Profile("!dev | dev-redis")
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800) // 30 minutes default
public class RedisSessionConfig {

    // Configuration is handled by Spring Boot auto-configuration
    // and application.yml properties when this class is active
}