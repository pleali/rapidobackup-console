package com.rapidobackup.console.user.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rapidobackup.console.contact.dto.ContactDto;
import com.rapidobackup.console.contact.entity.Contact;
import com.rapidobackup.console.contact.entity.ContactType;
import com.rapidobackup.console.user.dto.UserDto;
import com.rapidobackup.console.user.entity.User;
import com.rapidobackup.console.user.entity.UserRole;
import com.rapidobackup.console.user.entity.UserStatus;
import com.rapidobackup.console.user.repository.UserRepository;
import com.rapidobackup.console.contact.repository.ContactRepository;
import com.rapidobackup.console.tenant.repository.TenantRepository;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ContactRepository contactRepository;
  private final TenantRepository tenantRepository;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ContactRepository contactRepository, TenantRepository tenantRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.contactRepository = contactRepository;
    this.tenantRepository = tenantRepository;
  }

  public UserDto toDto(User user) {
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setActivated(user.isActive());
    // langKey moved to Contact.preferredLanguage
    dto.setImageUrl(null); // No imageUrl in current User entity
    dto.setRoles(user.getRoles());
    dto.setCreatedDate(user.getCreatedAt());
    dto.setLastModifiedDate(user.getUpdatedAt());
    dto.setLastLogin(user.getLastLoginAt());
    dto.setPasswordChangeRequired(user.getMustChangePassword() != null ? user.getMustChangePassword() : false);

    // Get data from Contact if present
    Contact contact = user.getContact();
    if (contact != null) {
      dto.setContact(contactToDto(contact));
    } else {
      dto.setContact(null);
    }

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

  private ContactDto contactToDto(Contact contact) {
    if (contact == null) {
      return null;
    }

    ContactDto dto = new ContactDto();
    dto.setId(contact.getId());
    dto.setContactType(contact.getContactType());
    dto.setIsPrimary(contact.getIsPrimary());

    // Personal information
    dto.setSalutation(contact.getSalutation());
    dto.setFirstName(contact.getFirstName());
    dto.setMiddleName(contact.getMiddleName());
    dto.setLastName(contact.getLastName());
    dto.setSuffix(contact.getSuffix());
    dto.setFullName(contact.getFullName());
    dto.setNickname(contact.getNickname());
    dto.setPreferredName(contact.getPreferredName());

    // Professional information
    dto.setCompanyName(contact.getCompanyName());
    dto.setJobTitle(contact.getJobTitle());
    dto.setDepartment(contact.getDepartment());
    dto.setEmployeeId(contact.getEmployeeId());
    dto.setDivision(contact.getDivision());

    // Communications
    dto.setEmail(contact.getEmail());
    dto.setPhonePrimary(contact.getPhonePrimary());
    dto.setPhoneSecondary(contact.getPhoneSecondary());
    dto.setPhoneMobile(contact.getPhoneMobile());
    dto.setFax(contact.getFax());

    // Address
    dto.setAddressLine1(contact.getAddressLine1());
    dto.setAddressLine2(contact.getAddressLine2());
    dto.setCity(contact.getCity());
    dto.setStateProvince(contact.getStateProvince());
    dto.setPostalCode(contact.getPostalCode());
    dto.setCountry(contact.getCountry());

    // Preferences
    dto.setPreferredContactMethod(contact.getPreferredContactMethod());
    dto.setPreferredLanguage(contact.getPreferredLanguage());
    dto.setTimezone(contact.getTimezone());

    // GDPR consents
    dto.setMarketingConsent(contact.getMarketingConsent());
    dto.setNewsletterConsent(contact.getNewsletterConsent());
    dto.setConsentDate(contact.getConsentDate());

    // Metadata
    dto.setNotes(contact.getNotes());
    dto.setTags(contact.getTags());
    dto.setCustomFields(contact.getCustomFields());

    // Verification
    dto.setEmailVerified(contact.getEmailVerified());
    dto.setPhoneVerified(contact.getPhoneVerified());
    dto.setVerifiedAt(contact.getVerifiedAt());

    // Timestamps
    dto.setCreatedAt(contact.getCreatedAt());
    dto.setUpdatedAt(contact.getUpdatedAt());

    return dto;
  }
}