package com.rapidobackup.console.user.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rapidobackup.console.user.entity.User;
import com.rapidobackup.console.user.entity.UserRole;
import com.rapidobackup.console.user.entity.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  Optional<User> findByActivationToken(String activationToken);

  Optional<User> findByResetKey(String resetKey);

  List<User> findByTenantId(UUID tenantId);

  Page<User> findByTenantId(UUID tenantId, Pageable pageable);

  @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r = :role")
  List<User> findByRole(@Param("role") UserRole role);

  List<User> findByStatusAndCreatedAtBefore(UserStatus status, Instant dateTime);

  List<User> findByResetDateBeforeAndResetKeyIsNotNull(Instant dateTime);

  long countByTenantId(UUID tenantId);

  @Query(
      "SELECT DISTINCT u FROM User u LEFT JOIN u.roles r WHERE "
          + "(:searchTerm IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
          + "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
          + "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
          + "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND "
          + "(:role IS NULL OR r = :role) AND "
          + "(:status IS NULL OR u.status = :status)")
  Page<User> findUsersWithFilters(
      @Param("searchTerm") String searchTerm,
      @Param("role") UserRole role,
      @Param("status") UserStatus status,
      Pageable pageable);

  @Modifying
  @Query("UPDATE User u SET u.lastLoginAt = :lastLoginAt WHERE u.id = :userId")
  void updateLastLogin(@Param("userId") UUID userId, @Param("lastLoginAt") Instant lastLoginAt);

  @Modifying
  @Query(
      "UPDATE User u SET u.failedLoginAttempts = :attempts, u.accountLockedUntil = :lockedUntil "
          + "WHERE u.id = :userId")
  void updateFailedLoginAttempts(
      @Param("userId") UUID userId,
      @Param("attempts") int attempts,
      @Param("lockedUntil") Instant lockedUntil);

  @Query(
      "SELECT u FROM User u WHERE u.tenant.id IN "
          + "(SELECT t.id FROM Tenant t WHERE t.path LIKE CONCAT(:tenantPath, '%'))")
  List<User> findByTenantHierarchy(@Param("tenantPath") String tenantPath);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}