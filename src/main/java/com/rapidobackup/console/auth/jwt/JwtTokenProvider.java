package com.rapidobackup.console.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

  private static final String AUTHORITIES_KEY = "auth";
  private static final String USER_ID_KEY = "userId";

  private final SecretKey key;
  private final long jwtExpirationMs;
  private final long refreshExpirationMs;

  public JwtTokenProvider(
      @Value("${console.auth.jwt.secret}") String jwtSecret,
      @Value("${console.auth.jwt.expiration:3600000}") long jwtExpirationMs,
      @Value("${console.auth.jwt.refresh-expiration:86400000}") long refreshExpirationMs) {
    this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    this.jwtExpirationMs = jwtExpirationMs;
    this.refreshExpirationMs = refreshExpirationMs;
  }

  public String generateAccessToken(Authentication authentication) {
    UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
    Date expiryDate = Date.from(Instant.now().plus(jwtExpirationMs, ChronoUnit.MILLIS));

    List<String> authorities =
        userPrincipal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

    return Jwts.builder()
        .subject(userPrincipal.getUsername())
        .claim(AUTHORITIES_KEY, authorities)
        .issuedAt(new Date())
        .expiration(expiryDate)
        .signWith(key)
        .compact();
  }

  public String generateAccessToken(String username, List<String> authorities, String userId) {
    Date expiryDate = Date.from(Instant.now().plus(jwtExpirationMs, ChronoUnit.MILLIS));

    return Jwts.builder()
        .subject(username)
        .claim(AUTHORITIES_KEY, authorities)
        .claim(USER_ID_KEY, userId)
        .issuedAt(new Date())
        .expiration(expiryDate)
        .signWith(key)
        .compact();
  }

  public String generateRefreshToken(String username) {
    Date expiryDate = Date.from(Instant.now().plus(refreshExpirationMs, ChronoUnit.MILLIS));

    return Jwts.builder()
        .subject(username)
        .claim("tokenType", "refresh")
        .issuedAt(new Date())
        .expiration(expiryDate)
        .signWith(key)
        .compact();
  }

  public String getUsernameFromToken(String token) {
    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    return claims.getSubject();
  }

  public String getUserIdFromToken(String token) {
    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    return claims.get(USER_ID_KEY, String.class);
  }

  @SuppressWarnings("unchecked")
  public List<String> getAuthoritiesFromToken(String token) {
    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    return claims.get(AUTHORITIES_KEY, List.class);
  }

  public boolean validateToken(String authToken) {
    try {
      Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
      return true;
    } catch (SecurityException ex) {
      logger.error("Invalid JWT signature: {}", ex.getMessage());
    } catch (MalformedJwtException ex) {
      logger.error("Invalid JWT token: {}", ex.getMessage());
    } catch (ExpiredJwtException ex) {
      logger.error("Expired JWT token: {}", ex.getMessage());
    } catch (UnsupportedJwtException ex) {
      logger.error("Unsupported JWT token: {}", ex.getMessage());
    } catch (IllegalArgumentException ex) {
      logger.error("JWT claims string is empty: {}", ex.getMessage());
    }
    return false;
  }

  public boolean isRefreshToken(String token) {
    try {
      Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
      return "refresh".equals(claims.get("tokenType"));
    } catch (Exception e) {
      return false;
    }
  }

  public long getExpirationFromToken(String token) {
    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    return claims.getExpiration().getTime();
  }
}