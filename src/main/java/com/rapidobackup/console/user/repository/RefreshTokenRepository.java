package com.rapidobackup.console.user.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rapidobackup.console.user.entity.RefreshToken;
import com.rapidobackup.console.user.entity.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

  Optional<RefreshToken> findByToken(String token);

  Optional<RefreshToken> findByUser(User user);

  void deleteByUser(User user);

  @Modifying
  @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate <= :now")
  void deleteAllExpiredTokens(@Param("now") Instant now);

  @Modifying
  @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user")
  void revokeAllUserTokens(@Param("user") User user);

  @Modifying
  @Query(
      "UPDATE RefreshToken rt SET rt.lastUsed = :lastUsed WHERE rt.token = :token AND rt.revoked ="
          + " false")
  void updateLastUsed(@Param("token") String token, @Param("lastUsed") Instant lastUsed);

  List<RefreshToken> findByUserAndRevokedFalse(User user);

  @Query(
      "SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND rt.revoked = false AND"
          + " rt.expiryDate > :now")
  List<RefreshToken> findActiveTokensByUser(@Param("user") User user, @Param("now") Instant now);

  long countByUserAndRevokedFalse(User user);
}