package com.rapidobackup.console.user.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import com.rapidobackup.console.contact.dto.ContactDto;
import com.rapidobackup.console.user.entity.UserRole;

public class UserDto {

  private UUID id;
  private String username;
  private boolean activated; // Computed from status
  private String imageUrl;
  private Set<UserRole> roles;
  private Instant createdAt;
  private Instant updatedAt;
  private Instant lastLoginAt;
  private boolean mustChangePassword;
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


  public boolean isActivated() {
    return activated;
  }

  public void setActivated(boolean activated) {
    this.activated = activated;
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

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Instant getLastLoginAt() {
    return lastLoginAt;
  }

  public void setLastLoginAt(Instant lastLoginAt) {
    this.lastLoginAt = lastLoginAt;
  }

  public boolean isMustChangePassword() {
    return mustChangePassword;
  }

  public void setMustChangePassword(boolean mustChangePassword) {
    this.mustChangePassword = mustChangePassword;
  }

  public ContactDto getContact() {
    return contact;
  }

  public void setContact(ContactDto contact) {
    this.contact = contact;
  }


  public boolean hasContact() {
    return contact != null;
  }
}
 