package com.rapidobackup.console.user.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rapidobackup.console.contact.entity.Contact;
import com.rapidobackup.console.contact.entity.ContactType;
import com.rapidobackup.console.contact.repository.ContactRepository;
import com.rapidobackup.console.tenant.repository.TenantRepository;
import com.rapidobackup.console.user.dto.UserDto;
import com.rapidobackup.console.user.entity.User;
import com.rapidobackup.console.user.entity.UserRole;
import com.rapidobackup.console.user.entity.UserStatus;
import com.rapidobackup.console.user.mapper.UserMapper;
import com.rapidobackup.console.user.repository.UserRepository;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ContactRepository contactRepository;
  private final TenantRepository tenantRepository;
  private final UserMapper userMapper;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ContactRepository contactRepository, TenantRepository tenantRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.contactRepository = contactRepository;
    this.tenantRepository = tenantRepository;
    this.userMapper = userMapper;
  }

  public UserDto toDto(User user) {
    return userMapper.toDto(user);
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
    User user = userRepository.findByIdWithContact(userId)
        .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    return toDto(user);
  }

  public User createUser(String username, String password, String langKey) {
    // langKey removed from User entity - now mapped to Contact.preferredLanguage
    User user = new User();
    user.setUsername(username);
    user.setPasswordHash(passwordEncoder.encode(password));
//TODO how to choose the selected tenant form context?
    // Assign default root tenant (UUID from Liquibase data)
    UUID rootTenantId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    user.setTenant(tenantRepository.getReferenceById(rootTenantId));

    // locale removed from User entity - now in Contact.preferredLanguage
    user.addRole(UserRole.USER); // Assign default USER role
    user.setStatus(UserStatus.ACTIVE);
    user.setCreatedAt(Instant.now());
    // lastModifiedBy and lastModifiedDate are set by JPA Auditing
    // user.setLastModifiedBy("system");
    // user.setCreatedBy("system");

    user.setUpdatedAt(Instant.now());

    return userRepository.save(user);
  }

  // Helper method to create user with contact
  public User createUserWithContact(String username, String password, String langKey,
                                   String email, String firstName, String lastName) {
    // Create user first
    User user = createUser(username, password, langKey);

    // Create associated contact if we have the required info
    if (email != null || firstName != null || lastName != null) {
      Contact contact = new Contact();
      contact.setTenant(user.getTenant());
      contact.setContactType(ContactType.PRIMARY);
      contact.setIsPrimary(true);
      contact.setEmail(email);
      contact.setFirstName(firstName);
      contact.setLastName(lastName);

      contact.setFullName((firstName != null && lastName != null) ? (firstName + " " + lastName).trim() : (firstName != null ? firstName : lastName));
      contact.setPreferredLanguage(langKey != null ? langKey : "en"); // langKey mapped to Contact.preferredLanguage
      contact.setEmailVerified(false);
      contact.setCreatedAt(Instant.now());
      contact.setUpdatedAt(Instant.now());

      // Save contact to the database
      contact = contactRepository.save(contact);

      // Link contact to user and save user again
      user.setContact(contact);
      user = userRepository.save(user);
    }

    return user;
  }

}