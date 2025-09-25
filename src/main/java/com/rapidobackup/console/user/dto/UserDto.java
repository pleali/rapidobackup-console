package com.rapidobackup.console.user.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import com.rapidobackup.console.contact.dto.ContactDto;
import com.rapidobackup.console.user.entity.UserRole;

public class UserDto {

  private UUID id;
  private String username;
  private String displayName;
  private String preferredName;
  private String email;
  private boolean activated;
  private String langKey;
  private String imageUrl;
  private Set<UserRole> roles;
  private UUID parentId;
  private String parentLogin;
  private Instant createdDate;
  private Instant lastModifiedDate;
  private Instant lastLogin;
  private boolean passwordChangeRequired;
  private ContactDto contact;

  public UserDto() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getPreferredName() {
    return preferredName;
  }

  public void setPreferredName(String preferredName) {
    this.preferredName = preferredName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean isActivated() {
    return activated;
  }

  public void setActivated(boolean activated) {
    this.activated = activated;
  }

  public String getLangKey() {
    return langKey;
  }

  public void setLangKey(String langKey) {
    this.langKey = langKey;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Set<UserRole> getRoles() {
    return roles;
  }

  public void setRoles(Set<UserRole> roles) {
    this.roles = roles;
  }

  public UUID getParentId() {
    return parentId;
  }

  public void setParentId(UUID parentId) {
    this.parentId = parentId;
  }

  public String getParentLogin() {
    return parentLogin;
  }

  public void setParentLogin(String parentLogin) {
    this.parentLogin = parentLogin;
  }

  public Instant getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Instant createdDate) {
    this.createdDate = createdDate;
  }

  public Instant getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(Instant lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public Instant getLastLogin() {
    return lastLogin;
  }

  public void setLastLogin(Instant lastLogin) {
    this.lastLogin = lastLogin;
  }

  public boolean isPasswordChangeRequired() {
    return passwordChangeRequired;
  }

  public void setPasswordChangeRequired(boolean passwordChangeRequired) {
    this.passwordChangeRequired = passwordChangeRequired;
  }

  public ContactDto getContact() {
    return contact;
  }

  public void setContact(ContactDto contact) {
    this.contact = contact;
  }

  public String getFullName() {
    if (displayName != null && !displayName.trim().isEmpty()) {
      return displayName;
    }
    if (preferredName != null && !preferredName.trim().isEmpty()) {
      return preferredName;
    }
    return username;
  }

  public boolean hasContact() {
    return contact != null;
  }
}