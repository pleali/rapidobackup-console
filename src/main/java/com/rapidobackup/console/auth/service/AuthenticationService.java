package com.rapidobackup.console.auth.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rapidobackup.console.auth.dto.AuthResponse;
import com.rapidobackup.console.auth.dto.LoginRequest;
import com.rapidobackup.console.auth.dto.SignupRequest;
import com.rapidobackup.console.auth.jwt.JwtTokenProvider;
import com.rapidobackup.console.common.exception.AuthenticationException;
import com.rapidobackup.console.common.exception.TokenRefreshException;
import com.rapidobackup.console.user.entity.RefreshToken;
import com.rapidobackup.console.user.entity.User;
import com.rapidobackup.console.user.repository.RefreshTokenRepository;
import com.rapidobackup.console.user.repository.UserRepository;
import com.rapidobackup.console.user.service.UserService;

@Service
@Transactional
public class AuthenticationService {

  private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider tokenProvider;
  private final UserService userService;
  private final long refreshTokenExpirationMs;

  public AuthenticationService(
      UserRepository userRepository,
      RefreshTokenRepository refreshTokenRepository,
      PasswordEncoder passwordEncoder,
      JwtTokenProvider tokenProvider,
      UserService userService,
      @Value("${console.auth.jwt.refresh-expiration:86400000}") long refreshTokenExpirationMs) {
    this.userRepository = userRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.tokenProvider = tokenProvider;
    this.userService = userService;
    this.refreshTokenExpirationMs = refreshTokenExpirationMs;
  }

  public AuthResponse authenticate(LoginRequest loginRequest) {
    User user =
        userRepository
            .findByLogin(loginRequest.getLogin())
            .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

    if (!user.isActivated()) {
      throw new AuthenticationException("User account is not activated");
    }

    if (user.isAccountLocked()) {
      throw new AuthenticationException("Account is locked due to too many failed attempts");
    }

    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
      handleFailedLogin(user);
      throw new AuthenticationException("Invalid credentials");
    }

    handleSuccessfulLogin(user);

    List<String> authorities = List.of(user.getRole().getAuthority());
    String accessToken =
        tokenProvider.generateAccessToken(user.getId().toString(), authorities);
    String refreshToken = createRefreshToken(user);

    return new AuthResponse(
        accessToken, refreshToken, refreshTokenExpirationMs / 1000, userService.toDto(user));
  }

  public AuthResponse refreshToken(String refreshTokenValue) {
    RefreshToken refreshToken = verifyRefreshToken(refreshTokenValue);
    User user = refreshToken.getUser();

    if (!user.isActivated()) {
      throw new AuthenticationException("User account is not activated");
    }

    if (user.isAccountLocked()) {
      throw new AuthenticationException("Account is locked");
    }

    List<String> authorities = List.of(user.getRole().getAuthority());
    String accessToken =
        tokenProvider.generateAccessToken(user.getId().toString(), authorities);

    refreshToken.setLastUsed(Instant.now());
    refreshTokenRepository.save(refreshToken);

    return new AuthResponse(
        accessToken, refreshTokenValue, refreshTokenExpirationMs / 1000, userService.toDto(user));
  }

  public void logout(String refreshTokenValue) {
    Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(refreshTokenValue);
    refreshToken.ifPresent(
        token -> {
          token.setRevoked(true);
          refreshTokenRepository.save(token);
        });
  }

  public void logoutAllDevices(String username) {
    userRepository
        .findByLogin(username)
        .ifPresent(user -> refreshTokenRepository.revokeAllUserTokens(user));
  }

  private String createRefreshToken(User user) {
    String tokenValue = tokenProvider.generateRefreshToken(user.getId().toString());

    Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
    if (existingToken.isPresent()) {
      RefreshToken token = existingToken.get();
      token.setToken(tokenValue);
      token.setExpiryDate(Instant.now().plus(refreshTokenExpirationMs, ChronoUnit.MILLIS));
      token.setRevoked(false);
      refreshTokenRepository.save(token);
    } else {
      RefreshToken refreshToken =
          new RefreshToken(
              user, tokenValue, Instant.now().plus(refreshTokenExpirationMs, ChronoUnit.MILLIS));
      refreshTokenRepository.save(refreshToken);
    }

    return tokenValue;
  }

  private RefreshToken verifyRefreshToken(String token) {
    RefreshToken refreshToken =
        refreshTokenRepository
            .findByToken(token)
            .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));

    if (refreshToken.isRevoked()) {
      throw new TokenRefreshException("Refresh token is revoked");
    }

    if (refreshToken.isExpired()) {
      refreshTokenRepository.delete(refreshToken);
      throw new TokenRefreshException("Refresh token expired");
    }

    if (!tokenProvider.validateToken(token)) {
      throw new TokenRefreshException("Invalid refresh token");
    }

    return refreshToken;
  }

  private void handleFailedLogin(User user) {
    int attempts = user.getFailedLoginAttempts() + 1;
    Instant lockUntil = null;

    if (attempts >= 5) {
      lockUntil = Instant.now().plus(30, ChronoUnit.MINUTES);
      logger.warn("Account locked for user: {} due to {} failed attempts", user.getLogin(), attempts);
    }

    userRepository.updateFailedLoginAttempts(user.getId(), attempts, lockUntil);
  }

  private void handleSuccessfulLogin(User user) {
    userRepository.updateFailedLoginAttempts(user.getId(), 0, null);
    userRepository.updateLastLogin(user.getId(), Instant.now());
  }

  public void changePassword(String username, String currentPassword, String newPassword) {
    User user =
        userRepository
            .findByLogin(username)
            .orElseThrow(() -> new AuthenticationException("User not found"));

    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
      throw new AuthenticationException("Current password is incorrect");
    }

    user.setPassword(passwordEncoder.encode(newPassword));
    user.setLastModifiedDate(Instant.now());
    user.setLastModifiedBy(username);
    userRepository.save(user);

    logger.info("Password changed for user: {}", username);
  }

  public void registerUser(SignupRequest signupRequest) {
    if (userRepository.findByLogin(signupRequest.getLogin()).isPresent()) {
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

  @Transactional
  public void cleanupExpiredTokens() {
    refreshTokenRepository.deleteAllExpiredTokens(Instant.now());
    logger.debug("Cleaned up expired refresh tokens");
  }
}