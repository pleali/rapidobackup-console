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

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByLogin(String login);

  Optional<User> findByEmail(String email);

  Optional<User> findByActivationKey(String activationKey);

  Optional<User> findByResetKey(String resetKey);

  List<User> findByParentId(UUID parentId);

  Page<User> findByParentId(UUID parentId, Pageable pageable);

  @Query(
      "SELECT u FROM User u WHERE u.parent.id = :parentId OR "
          + "(u.parent IS NULL AND :parentId IS NULL)")
  List<User> findByParentIdIncludingNull(@Param("parentId") UUID parentId);

  List<User> findByRole(UserRole role);

  List<User> findByActivatedFalseAndCreatedDateBefore(Instant dateTime);

  List<User> findByResetDateBeforeAndResetKeyIsNotNull(Instant dateTime);

  @Query(
      "SELECT COUNT(u) FROM User u WHERE u.parent.id = :parentId OR "
          + "(u.parent IS NULL AND :parentId IS NULL)")
  long countByParentId(@Param("parentId") UUID parentId);

  @Query(
      "SELECT u FROM User u WHERE "
          + "(:searchTerm IS NULL OR LOWER(u.login) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
          + "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
          + "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
          + "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND "
          + "(:role IS NULL OR u.role = :role) AND "
          + "(:activated IS NULL OR u.activated = :activated)")
  Page<User> findUsersWithFilters(
      @Param("searchTerm") String searchTerm,
      @Param("role") UserRole role,
      @Param("activated") Boolean activated,
      Pageable pageable);

  @Modifying
  @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.id = :userId")
  void updateLastLogin(@Param("userId") UUID userId, @Param("lastLogin") Instant lastLogin);

  @Modifying
  @Query(
      "UPDATE User u SET u.failedLoginAttempts = :attempts, u.accountLockedUntil = :lockedUntil "
          + "WHERE u.id = :userId")
  void updateFailedLoginAttempts(
      @Param("userId") UUID userId,
      @Param("attempts") int attempts,
      @Param("lockedUntil") Instant lockedUntil);

  @Query(
      "SELECT u FROM User u WHERE "
          + "u.id = :userId OR u.parent.id = :userId OR "
          + "u.parent.parent.id = :userId OR u.parent.parent.parent.id = :userId")
  List<User> findUserHierarchy(@Param("userId") UUID userId);

  boolean existsByLogin(String login);

  boolean existsByEmail(String email);
}