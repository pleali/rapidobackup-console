package com.rapidobackup.console.common.exception;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private static final String PROBLEM_BASE_URL = "https://rapidobackup.com/problems";

  @ExceptionHandler(AuthenticationException.class)
  public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.UNAUTHORIZED, ex.getMessage());
    problemDetail.setType(URI.create(PROBLEM_BASE_URL + "/authentication-failed"));
    problemDetail.setTitle("Authentication Failed");

    logger.warn("Authentication error: {}", ex.getMessage());
    return problemDetail;
  }

  @ExceptionHandler(TokenRefreshException.class)
  public ProblemDetail handleTokenRefreshException(TokenRefreshException ex) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.UNAUTHORIZED, ex.getMessage());
    problemDetail.setType(URI.create(PROBLEM_BASE_URL + "/token-refresh-failed"));
    problemDetail.setTitle("Token Refresh Failed");

    logger.warn("Token refresh error: {}", ex.getMessage());
    return problemDetail;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGenericException(Exception ex) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    problemDetail.setType(URI.create(PROBLEM_BASE_URL + "/internal-server-error"));
    problemDetail.setTitle("Internal Server Error");

    logger.error("Unexpected error: {}", ex.getMessage(), ex);
    return problemDetail;
  }
}