package com.rapidobackup.console.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rapidobackup.console.auth.dto.AuthResponse;
import com.rapidobackup.console.auth.dto.LoginRequest;
import com.rapidobackup.console.auth.dto.PasswordChangeRequest;
import com.rapidobackup.console.auth.dto.RefreshTokenRequest;
import com.rapidobackup.console.auth.service.AuthenticationService;
import com.rapidobackup.console.common.dto.MessageResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  private final AuthenticationService authenticationService;

  public AuthController(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(
      @Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
    
    logger.info("Login attempt for user: {} from IP: {}", 
        loginRequest.getLogin(), getClientIp(request));
    
    AuthResponse authResponse = authenticationService.authenticate(loginRequest);
    
    logger.info("Successful login for user: {}", loginRequest.getLogin());
    
    return ResponseEntity.ok(authResponse);
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refreshToken(
      @Valid @RequestBody RefreshTokenRequest request) {
    
    logger.debug("Token refresh requested");
    
    AuthResponse authResponse = authenticationService.refreshToken(request.getRefreshToken());
    
    return ResponseEntity.ok(authResponse);
  }

  @PostMapping("/logout")
  public ResponseEntity<MessageResponse> logout(@Valid @RequestBody RefreshTokenRequest request) {
    
    logger.debug("Logout requested");
    
    authenticationService.logout(request.getRefreshToken());
    
    return ResponseEntity.ok(new MessageResponse("Logout successful"));
  }

  @PostMapping("/logout-all")
  public ResponseEntity<MessageResponse> logoutAllDevices(Principal principal) {
    
    logger.info("Logout from all devices requested by user: {}", principal.getName());
    
    authenticationService.logoutAllDevices(principal.getName());
    
    return ResponseEntity.ok(new MessageResponse("Logged out from all devices"));
  }

  @PostMapping("/change-password")
  public ResponseEntity<MessageResponse> changePassword(
      @Valid @RequestBody PasswordChangeRequest request, Principal principal) {
    
    logger.info("Password change requested by user: {}", principal.getName());
    
    authenticationService.changePassword(
        principal.getName(), request.getCurrentPassword(), request.getNewPassword());
    
    return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
  }

  private String getClientIp(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }
    
    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty()) {
      return xRealIp;
    }
    
    return request.getRemoteAddr();
  }
}