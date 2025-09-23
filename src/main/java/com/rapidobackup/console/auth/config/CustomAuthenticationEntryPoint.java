package com.rapidobackup.console.auth.config;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom AuthenticationEntryPoint that returns ProblemDetail responses for 401 Unauthorized errors.
 *
 * This component handles authentication failures that occur in Spring Security filters
 * before reaching the application controllers, ensuring consistent error response format.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);
    private static final String PROBLEM_BASE_URL = "about:blank";

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        logger.warn("Authentication failed for request to {}: {}",
                   request.getRequestURI(), authException.getMessage());

        // Create ProblemDetail for authentication failure
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            "Authentication failed"
        );

        problemDetail.setType(URI.create(PROBLEM_BASE_URL + "/authentication-required"));
        problemDetail.setTitle("Authentication Required");
        problemDetail.setProperty("path", request.getRequestURI());
        problemDetail.setProperty("method", request.getMethod());

        // Set response headers
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Add WWW-Authenticate header as per RFC 7235
        response.setHeader("WWW-Authenticate", "FormBased realm=\"Console\"");

        // Write ProblemDetail as JSON response
        String jsonResponse = objectMapper.writeValueAsString(problemDetail);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}