package com.rapidobackup.console.config;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Session configuration for development and production environments
 * - Development: In-memory sessions (MapSessionRepository)
 * - Production: Redis-backed sessions
 */
public class SessionConfig {

    /**
     * Configuration for development environment - In-memory sessions
     */
    @Configuration
    @Profile("dev")
    @EnableSpringHttpSession
    public static class DevSessionConfig {

        @Bean
        public SessionRepository<?> sessionRepository() {
            return new MapSessionRepository(new ConcurrentHashMap<>());
        }

        @Bean
        public CookieSerializer cookieSerializer() {
            DefaultCookieSerializer serializer = new DefaultCookieSerializer();
            serializer.setCookieName("JSESSIONID");
            serializer.setUseHttpOnlyCookie(true);
            serializer.setUseSecureCookie(false); // HTTP for development
            serializer.setSameSite("Lax"); // Relaxed for development
            serializer.setCookiePath("/");
            serializer.setCookieMaxAge((int) Duration.ofMinutes(30).getSeconds());
            return serializer;
        }
    }

    /**
     * Configuration for production environment - Redis sessions
     */
    @Configuration
    @Profile("prod")
    @EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800) // 30 minutes
    public static class ProdSessionConfig {

        @Bean
        public CookieSerializer cookieSerializer() {
            DefaultCookieSerializer serializer = new DefaultCookieSerializer();
            serializer.setCookieName("JSESSIONID");
            serializer.setUseHttpOnlyCookie(true);
            serializer.setUseSecureCookie(true); // HTTPS required in production
            serializer.setSameSite("Strict"); // Strict security in production
            serializer.setCookiePath("/");
            serializer.setCookieMaxAge((int) Duration.ofMinutes(30).getSeconds());
            return serializer;
        }
    }
}