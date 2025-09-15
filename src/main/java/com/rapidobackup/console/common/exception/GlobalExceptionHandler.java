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

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidationExceptions(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST, "Validation failed");
    problemDetail.setType(URI.create("https://rapidobackup.com/problems/validation-failed"));
    problemDetail.setTitle("Validation Failed");
    problemDetail.setInstance(URI.create(request.getRequestURI()));

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

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.METHOD_NOT_ALLOWED,
        "HTTP method '" + ex.getMethod() + "' is not supported for this endpoint");
    problemDetail.setType(URI.create("https://rapidobackup.com/problems/method-not-allowed"));
    problemDetail.setTitle("Method Not Allowed");
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    problemDetail.setProperty("method", ex.getMethod());
    problemDetail.setProperty("supportedMethods", ex.getSupportedHttpMethods());

    logger.warn("Method not allowed on {}: {}", request.getRequestURI(), ex.getMethod());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(problemDetail);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ProblemDetail> handleMissingParameter(
      MissingServletRequestParameterException ex, HttpServletRequest request) {

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        "Required parameter '" + ex.getParameterName() + "' is missing");
    problemDetail.setType(URI.create("https://rapidobackup.com/problems/missing-parameter"));
    problemDetail.setTitle("Missing Parameter");
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    problemDetail.setProperty("parameterName", ex.getParameterName());
    problemDetail.setProperty("parameterType", ex.getParameterType());

    logger.warn("Missing parameter on {}: {}", request.getRequestURI(), ex.getParameterName());
    return ResponseEntity.badRequest().body(problemDetail);
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ProblemDetail> handleMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.UNSUPPORTED_MEDIA_TYPE,
        "Media type '" + ex.getContentType() + "' is not supported");
    problemDetail.setType(URI.create("https://rapidobackup.com/problems/unsupported-media-type"));
    problemDetail.setTitle("Unsupported Media Type");
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    problemDetail.setProperty("contentType", ex.getContentType());
    problemDetail.setProperty("supportedMediaTypes", ex.getSupportedMediaTypes());

    logger.warn("Unsupported media type on {}: {}", request.getRequestURI(), ex.getContentType());
    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(problemDetail);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ProblemDetail> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.FORBIDDEN, "Access denied");
    problemDetail.setType(URI.create("https://rapidobackup.com/problems/access-denied"));
    problemDetail.setTitle("Access Denied");
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    logger.warn("Access denied on {}: {}", request.getRequestURI(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ProblemDetail> handleAuthenticationException(
      AuthenticationException ex, HttpServletRequest request) {

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.UNAUTHORIZED, ex.getMessage());
    problemDetail.setType(URI.create("https://rapidobackup.com/problems/authentication-failed"));
    problemDetail.setTitle("Authentication Failed");
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    logger.warn("Authentication error on {}: {}", request.getRequestURI(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
  }

  @ExceptionHandler(TokenRefreshException.class)
  public ResponseEntity<ProblemDetail> handleTokenRefreshException(
      TokenRefreshException ex, HttpServletRequest request) {

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.UNAUTHORIZED, ex.getMessage());
    problemDetail.setType(URI.create("https://rapidobackup.com/problems/token-refresh-failed"));
    problemDetail.setTitle("Token Refresh Failed");
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    logger.warn("Token refresh error on {}: {}", request.getRequestURI(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleGenericException(
      Exception ex, HttpServletRequest request) {

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    problemDetail.setType(URI.create("https://rapidobackup.com/problems/internal-server-error"));
    problemDetail.setTitle("Internal Server Error");
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    logger.error("Unexpected error on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
  }
}