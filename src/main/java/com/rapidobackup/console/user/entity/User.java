package com.rapidobackup.console.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "console_users")
public class User {

  @Id
  @GeneratedValue
  private UUID id;

  @NotBlank
  @Size(min = 3, max = 50)
  @Column(name = "login", length = 50, unique = true, nullable = false)
  private String login;

  @JsonIgnore
  @NotBlank
  @Size(min = 60, max = 60)
  @Column(name = "password_hash", length = 60, nullable = false)
  private String password;

  @Size(max = 50)
  @Column(name = "first_name", length = 50)
  private String firstName;

  @Size(max = 50)
  @Column(name = "last_name", length = 50)
  private String lastName;

  @Email
  @Size(min = 5, max = 254)
  @Column(name = "email", length = 254, unique = true, nullable = false)
  private String email;

  @Column(name = "activated", nullable = false)
  private boolean activated = false;

  @Size(min = 2, max = 10)
  @Column(name = "lang_key", length = 10)
  private String langKey = "en";

  @Size(max = 256)
  @Column(name = "image_url", length = 256)
  private String imageUrl;

  @Size(max = 20)
  @Column(name = "activation_key", length = 20)
  @JsonIgnore
  private String activationKey;

  @Size(max = 20)
  @Column(name = "reset_key", length = 20)
  @JsonIgnore
  private String resetKey;

  @Column(name = "reset_date")
  private Instant resetDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private UserRole role = UserRole.CLIENT;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private User parent;

  @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
  private Set<User> children = new HashSet<>();

  @Size(max = 50)
  @Column(name = "created_by", length = 50)
  private String createdBy;

  @Column(name = "created_date", nullable = false)
  private Instant createdDate = Instant.now();

  @Size(max = 50)
  @Column(name = "last_modified_by", length = 50)
  private String lastModifiedBy;

  @Column(name = "last_modified_date")
  private Instant lastModifiedDate = Instant.now();

  @Column(name = "last_login")
  private Instant lastLogin;

  @Column(name = "failed_login_attempts")
  private Integer failedLoginAttempts = 0;

  @Column(name = "account_locked_until")
  private Instant accountLockedUntil;

  public User() {}

  public User(String login, String password, String email, UserRole role) {
    this.login = login;
    this.password = password;
    this.email = email;
    this.role = role;
  }

  // Getters and Setters

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
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

  public String getActivationKey() {
    return activationKey;
  }

  public void setActivationKey(String activationKey) {
    this.activationKey = activationKey;
  }

  public String getResetKey() {
    return resetKey;
  }

  public void setResetKey(String resetKey) {
    this.resetKey = resetKey;
  }

  public Instant getResetDate() {
    return resetDate;
  }

  public void setResetDate(Instant resetDate) {
    this.resetDate = resetDate;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }

  public User getParent() {
    return parent;
  }

  public void setParent(User parent) {
    this.parent = parent;
  }

  public Set<User> getChildren() {
    return children;
  }

  public void setChildren(Set<User> children) {
    this.children = children;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Instant getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Instant createdDate) {
    this.createdDate = createdDate;
  }

  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
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

  public Integer getFailedLoginAttempts() {
    return failedLoginAttempts;
  }

  public void setFailedLoginAttempts(Integer failedLoginAttempts) {
    this.failedLoginAttempts = failedLoginAttempts;
  }

  public Instant getAccountLockedUntil() {
    return accountLockedUntil;
  }

  public void setAccountLockedUntil(Instant accountLockedUntil) {
    this.accountLockedUntil = accountLockedUntil;
  }

  public boolean isAccountLocked() {
    return accountLockedUntil != null && Instant.now().isBefore(accountLockedUntil);
  }

  public String getFullName() {
    if (firstName != null && lastName != null) {
      return firstName + " " + lastName;
    }
    return login;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User)) return false;
    return id != null && id.equals(((User) o).id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "User{" + "login='" + login + '\'' + ", firstName='" + firstName + '\'' + ", lastName='"
        + lastName + '\'' + ", email='" + email + '\'' + ", activated=" + activated + '}';
  }
}