package com.rapidobackup.console.auth.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.rapidobackup.console.web.filter.SpaWebFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;

  public SecurityConfig(CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                       CustomAccessDeniedHandler customAccessDeniedHandler) {
    this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    this.customAccessDeniedHandler = customAccessDeniedHandler;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    Map<String, PasswordEncoder> encoders = new HashMap<>();
    
    // Primary encoder for new passwords
    BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder(12);
    encoders.put("bcrypt", bcryptEncoder);
    
    // Legacy MD5 encoder for migration (deprecated, security warning suppressed for migration purpose)
    @SuppressWarnings("deprecation")
    MessageDigestPasswordEncoder md5Encoder = new MessageDigestPasswordEncoder("MD5");
    encoders.put("md5", md5Encoder);
    
    // Create delegating encoder with bcrypt as default
    DelegatingPasswordEncoder delegatingEncoder = new DelegatingPasswordEncoder("bcrypt", encoders);
    
    // Allow legacy passwords without prefix to be treated as MD5 during migration
    delegatingEncoder.setDefaultPasswordEncoderForMatches(md5Encoder);
    
    return delegatingEncoder;
  }


  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  @Order(1)
  public SecurityFilterChain agentApiSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.securityMatcher("/api/agent-polling/**", "/ws/agent/**")
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDeniedHandler))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/agent-polling/**", "/ws/agent/**")
                    .permitAll() // Agent authentication handled separately
            )
        .build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain openApiSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.securityMatcher("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**")
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDeniedHandler))
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .build();
  }

  @Bean
  @Order(3)
  public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.securityMatcher("/api/**")
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for API endpoints
        .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED))
        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDeniedHandler))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/api/auth/login",
                        "/api/auth/signup",
                        "/api/public/**",
                        "/api/test/**",
                        "/api/actuator/health/**")
                    .permitAll()
                    .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/management/**")
                    .hasAnyRole("ADMIN", "GROSSISTE")
                    .requestMatchers("/api/partner/**")
                    .hasAnyRole("ADMIN", "GROSSISTE", "PARTENAIRE")
                    .anyRequest()
                    .authenticated())
        .build();
  }

  @Bean
  @Order(4)
  public SecurityFilterChain webSocketSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.securityMatcher("/ws/**")
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED))
        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDeniedHandler))
        .authorizeHttpRequests(auth -> auth.requestMatchers("/ws/**").authenticated())
        .build();
  }

  @Bean
  @Order(5)
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .addFilterAfter(new SpaWebFilter(), BasicAuthenticationFilter.class )
        .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED))
        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDeniedHandler))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/",
                        "/*.svg",
                        "/index.html",
                        "/static/**",
                        "/assets/**",
                        "/favicon.ico",
                        "/manifest.json",
                        "/actuator/health/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // Allow specific origins in production, * for development
    configuration.addAllowedOriginPattern("*");
    
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    
    return source;
  }
}