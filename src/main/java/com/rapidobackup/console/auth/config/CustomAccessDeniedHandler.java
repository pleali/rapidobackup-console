package com.rapidobackup.console.auth.config;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom AccessDeniedHandler that returns ProblemDetail responses for 403 Forbidden errors.
 *
 * This component handles authorization failures that occur in Spring Security filters
 * when an authenticated user lacks sufficient permissions for the requested resource.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    private static final String PROBLEM_BASE_URL = "https://rapidobackup.com/problems";

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // Get current authentication for logging purposes
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "anonymous";

        logger.warn("Access denied for user '{}' on request to {}: {}",
                   username, request.getRequestURI(), accessDeniedException.getMessage());

        // Create ProblemDetail for access denied error
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.FORBIDDEN,
            "Access denied"
        );

        problemDetail.setType(URI.create(PROBLEM_BASE_URL + "/access-denied"));
        problemDetail.setTitle("Access Denied");
        problemDetail.setProperty("path", request.getRequestURI());
        problemDetail.setProperty("method", request.getMethod());
        problemDetail.setProperty("user", username);

        // Set response headers
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Write ProblemDetail as JSON response
        String jsonResponse = objectMapper.writeValueAsString(problemDetail);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}