package com.rapidobackup.console.user.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rapidobackup.console.user.dto.UserDto;
import com.rapidobackup.console.user.entity.User;
import com.rapidobackup.console.user.entity.UserRole;
import com.rapidobackup.console.user.entity.UserStatus;
import com.rapidobackup.console.user.repository.UserRepository;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UserDto toDto(User user) {
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setDisplayName(user.getDisplayName());
    dto.setPreferredName(user.getPreferredName());
    dto.setEmail(user.getEmail());
    dto.setActivated(user.isActive());
    dto.setLangKey(user.getPreferredLanguage());
    dto.setImageUrl(null); // No imageUrl in current User entity
    dto.setRoles(user.getRoles());
    dto.setCreatedDate(user.getCreatedAt());
    dto.setLastModifiedDate(user.getUpdatedAt());
    dto.setLastLogin(user.getLastLoginAt());
    dto.setPasswordChangeRequired(user.getMustChangePassword() != null ? user.getMustChangePassword() : false);

    // Parent relationship not implemented in current User entity
    // Parent relationship not implemented in current User entity
    // if (user.getParent() != null) {
    //   dto.setParentId(user.getParent().getId());
    //   dto.setParentLogin(user.getParent().getUsername());
    // }

    return dto;
  }

  public UserDto findByUsername(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found: " + username));
    return toDto(user);
  }

  public UserDto findById(String userId) {
    UUID uuid = UUID.fromString(userId);
    User user = userRepository.findById(uuid)
        .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    return toDto(user);
  }

  public UserDto findById(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    return toDto(user);
  }

  public User createUser(String username, String email, String password, String displayName, String preferredName, String langKey) {
    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(password));
    user.setDisplayName(displayName != null ? displayName : username);
    user.setPreferredName(preferredName);
    user.setPreferredLanguage(langKey != null ? langKey : "en");
    user.addRole(UserRole.USER); // Assign default USER role
    user.setStatus(UserStatus.ACTIVE);
    user.setCreatedAt(Instant.now());
    // lastModifiedBy and lastModifiedDate are set by JPA Auditing
    // user.setLastModifiedBy("system");
    // user.setCreatedBy("system");

    user.setUpdatedAt(Instant.now());
    
    return userRepository.save(user);
  }
}