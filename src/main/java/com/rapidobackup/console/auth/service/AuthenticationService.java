package com.rapidobackup.console.auth.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rapidobackup.console.auth.dto.LoginRequest;
import com.rapidobackup.console.auth.dto.SignupRequest;
import com.rapidobackup.console.common.exception.AuthenticationException;
import com.rapidobackup.console.user.dto.UserDto;
import com.rapidobackup.console.user.entity.User;
import com.rapidobackup.console.user.repository.UserRepository;
import com.rapidobackup.console.user.service.UserService;

@Service
@Transactional
public class AuthenticationService {

  private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final UserService userService;

  public AuthenticationService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      UserService userService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.userService = userService;
  }

  public UserDto authenticate(LoginRequest loginRequest) {
    try {
      // Use Spring Security's AuthenticationManager for proper session creation
      Authentication authRequest = new UsernamePasswordAuthenticationToken(
          loginRequest.getLogin(), loginRequest.getPassword());

      Authentication authResult = authenticationManager.authenticate(authRequest);

      // Set authentication in security context (this creates the session)
      SecurityContextHolder.getContext().setAuthentication(authResult);

      // Get the authenticated user for additional processing
      User user = userRepository
          .findByUsername(loginRequest.getLogin())
          .orElseThrow(() -> new AuthenticationException("User not found after authentication"));

      // Check if password needs upgrade from MD5 to BCrypt
      if (needsPasswordUpgrade(user.getPasswordHash())) {
        upgradePassword(user, loginRequest.getPassword());
      }

      handleSuccessfulLogin(user);

      logger.info("User authenticated successfully: {}", user.getUsername());
      return userService.toDto(user);

    } catch (org.springframework.security.core.AuthenticationException e) {
      // Handle Spring Security authentication exceptions
      User user = userRepository.findByUsername(loginRequest.getLogin()).orElse(null);
      if (user != null) {
        handleFailedLogin(user);
      }
      throw new AuthenticationException("Invalid credentials");
    }
  }

  public void logout() {
    // Clear Spring Security context
    SecurityContextHolder.clearContext();
    logger.info("User logged out successfully");
  }


  private void handleFailedLogin(User user) {
    int attempts = user.getFailedLoginAttempts() + 1;
    Instant lockUntil = null;

    if (attempts >= 5) {
      lockUntil = Instant.now().plus(30, ChronoUnit.MINUTES);
      logger.warn("Account locked for user: {} due to {} failed attempts", user.getUsername(), attempts);
    }

    userRepository.updateFailedLoginAttempts(user.getId(), attempts, lockUntil);
  }

  private void handleSuccessfulLogin(User user) {
    userRepository.updateFailedLoginAttempts(user.getId(), 0, null);
    userRepository.updateLastLogin(user.getId(), Instant.now());
  }

  public void changePassword(String userId, String currentPassword, String newPassword) {
    User user =
        userRepository
            .findById(java.util.UUID.fromString(userId))
            .orElseThrow(() -> new AuthenticationException("User not found"));

    if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
      throw new AuthenticationException("Current password is incorrect");
    }

    user.setPasswordHash(passwordEncoder.encode(newPassword));
    user.setMustChangePassword(false);
    userRepository.save(user);

    logger.info("Password changed for user: {} ({})", user.getUsername(), userId);
  }

  public void registerUser(SignupRequest signupRequest) {
    if (userRepository.findByUsername(signupRequest.getLogin()).isPresent()) {
      throw new AuthenticationException("Username is already taken");
    }
    
    if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
      throw new AuthenticationException("Email is already registered");
    }
    
    userService.createUser(
        signupRequest.getLogin(),
        signupRequest.getEmail(),
        signupRequest.getPassword(),
        signupRequest.getFirstName(),
        signupRequest.getLastName(),
        signupRequest.getLangKey()
    );
    
    logger.info("User registered successfully: {}", signupRequest.getLogin());
  }


  private boolean needsPasswordUpgrade(String storedPassword) {
    // Password needs upgrade if it doesn't start with {bcrypt} or has no algorithm prefix
    return !storedPassword.startsWith("{bcrypt}");
  }

  private void upgradePassword(User user, String plainTextPassword) {
    try {
      // Re-encode with BCrypt (DelegatingPasswordEncoder will add {bcrypt} prefix)
      String upgradedPassword = passwordEncoder.encode(plainTextPassword);
      user.setPasswordHash(upgradedPassword);
      userRepository.save(user);
      
      logger.info("Password upgraded from legacy format to BCrypt for user: {}", user.getUsername());
    } catch (Exception e) {
      logger.error("Failed to upgrade password for user: {}", user.getUsername(), e);
      // Don't throw exception to avoid breaking login flow
    }
  }
}