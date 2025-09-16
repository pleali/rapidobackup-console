package com.rapidobackup.console.auth.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rapidobackup.console.auth.dto.LoginRequest;
import com.rapidobackup.console.auth.dto.PasswordChangeRequest;
import com.rapidobackup.console.auth.dto.SignupRequest;
import com.rapidobackup.console.auth.service.AuthenticationService;
import com.rapidobackup.console.common.dto.MessageResponse;
import com.rapidobackup.console.user.dto.UserDto;
import com.rapidobackup.console.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  private final AuthenticationService authenticationService;
  private final UserService userService;

  public AuthController(AuthenticationService authenticationService, UserService userService) {
    this.authenticationService = authenticationService;
    this.userService = userService;
  }

  @PostMapping("/login")
  public ResponseEntity<UserDto> login(
      @Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {

    logger.info("Login attempt for user: {} from IP: {}",
        loginRequest.getLogin(), getClientIp(request));

    UserDto user = authenticationService.authenticate(loginRequest);

    logger.info("Successful login for user: {}", loginRequest.getLogin());

    return ResponseEntity.ok(user);
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
    if (principal == null) {
      return ResponseEntity.status(401).build();
    }

    UserDto user = userService.findByLogin(principal.getName());
    return ResponseEntity.ok(user);
  }

  @PostMapping("/signup")
  public ResponseEntity<MessageResponse> signup(
      @Valid @RequestBody SignupRequest signupRequest, HttpServletRequest request) {
    
    logger.info("Signup attempt for user: {} from IP: {}", 
        signupRequest.getLogin(), getClientIp(request));
    
    authenticationService.registerUser(signupRequest);
    
    logger.info("Successful registration for user: {}", signupRequest.getLogin());
    
    return ResponseEntity.ok(new MessageResponse("User registered successfully"));
  }

  @PostMapping("/logout")
  public ResponseEntity<MessageResponse> logout(HttpServletRequest request) {

    logger.debug("Logout requested");

    authenticationService.logout();

    // Invalidate session
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }

    return ResponseEntity.ok(new MessageResponse("Logout successful"));
  }

  @PostMapping("/change-password")
  public ResponseEntity<MessageResponse> changePassword(
      @Valid @RequestBody PasswordChangeRequest request, Principal principal) {
    
    logger.info("Password change requested by user ID: {}", principal.getName());
    
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