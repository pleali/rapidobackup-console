package com.rapidobackup.console.common.exception;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "com.rapidobackup.console")
public class GlobalExceptionHandler {

  // TODO: Add translations for error messages.

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private static final String PROBLEM_BASE_URL = "https://rapidobackup.com/problems";

  private ProblemDetail createProblemDetail(HttpStatus status, String type, String title,
      String detail, HttpServletRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
    problemDetail.setType(URI.create(PROBLEM_BASE_URL + "/" + type));
    problemDetail.setTitle(title);
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    problemDetail.setProperty("status", status.value());
    return problemDetail;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidationExceptions(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    ProblemDetail problemDetail = createProblemDetail(
        HttpStatus.BAD_REQUEST, "validation-failed", "Validation Failed",
        "Validation failed", request);

    List<String> errors = new ArrayList<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.add(fieldName + ": " + errorMessage);
    });
    problemDetail.setProperty("errors", errors);

    logger.warn("Validation error on {}: {}", request.getRequestURI(), errors);
    return ResponseEntity.badRequest().body(problemDetail);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ProblemDetail> handleMethodNotSupported(
      HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

    ProblemDetail problemDetail = createProblemDetail(
        HttpStatus.METHOD_NOT_ALLOWED, "method-not-allowed", "Method Not Allowed",
        "HTTP method '" + ex.getMethod() + "' is not supported for this endpoint", request);
    problemDetail.setProperty("method", ex.getMethod());
    problemDetail.setProperty("supportedMethods", ex.getSupportedHttpMethods());

    logger.warn("Method not allowed on {}: {}", request.getRequestURI(), ex.getMethod());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(problemDetail);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ProblemDetail> handleMissingParameter(
      MissingServletRequestParameterException ex, HttpServletRequest request) {

    ProblemDetail problemDetail = createProblemDetail(
        HttpStatus.BAD_REQUEST, "missing-parameter", "Missing Parameter",
        "Required parameter '" + ex.getParameterName() + "' is missing", request);
    problemDetail.setProperty("parameterName", ex.getParameterName());
    problemDetail.setProperty("parameterType", ex.getParameterType());

    logger.warn("Missing parameter on {}: {}", request.getRequestURI(), ex.getParameterName());
    return ResponseEntity.badRequest().body(problemDetail);
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ProblemDetail> handleMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {

    ProblemDetail problemDetail = createProblemDetail(
        HttpStatus.UNSUPPORTED_MEDIA_TYPE, "unsupported-media-type", "Unsupported Media Type",
        "Media type '" + ex.getContentType() + "' is not supported", request);
    problemDetail.setProperty("contentType", ex.getContentType());
    problemDetail.setProperty("supportedMediaTypes", ex.getSupportedMediaTypes());

    logger.warn("Unsupported media type on {}: {}", request.getRequestURI(), ex.getContentType());
    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(problemDetail);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ProblemDetail> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {

    ProblemDetail problemDetail = createProblemDetail(
        HttpStatus.FORBIDDEN, "access-denied", "Access Denied",
        "Access denied", request);

    logger.warn("Access denied on {}: {}", request.getRequestURI(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ProblemDetail> handleAuthenticationException(
      AuthenticationException ex, HttpServletRequest request) {

    ProblemDetail problemDetail = createProblemDetail(
        HttpStatus.UNAUTHORIZED, "authentication-failed", "Authentication Failed",
        ex.getMessage(), request);

    logger.warn("Authentication error on {}: {}", request.getRequestURI(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
  }

  @ExceptionHandler(TokenRefreshException.class)
  public ResponseEntity<ProblemDetail> handleTokenRefreshException(
      TokenRefreshException ex, HttpServletRequest request) {

    ProblemDetail problemDetail = createProblemDetail(
        HttpStatus.UNAUTHORIZED, "token-refresh-failed", "Token Refresh Failed",
        ex.getMessage(), request);

    logger.warn("Token refresh error on {}: {}", request.getRequestURI(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleGenericException(
      Exception ex, HttpServletRequest request) {

    ProblemDetail problemDetail = createProblemDetail(
        HttpStatus.INTERNAL_SERVER_ERROR, "internal-server-error", "Internal Server Error",
        "An unexpected error occurred", request);

    logger.error("Unexpected error on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
  }
}