package com.rapidobackup.console.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {

  @NotBlank
  @Size(min = 3, max = 50)
  private String username;

  @NotBlank
  @Email
  @Size(min = 5, max = 100)
  private String email;

  @NotBlank
  @Size(min = 6, max = 100)
  private String password;

  @NotBlank
  @Size(max = 255)
  private String displayName;

  @Size(max = 100)
  private String preferredName;

  @Size(min = 2, max = 10)
  private String langKey = "en";

  public SignupRequest() {}

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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

  public String getLangKey() {
    return langKey;
  }

  public void setLangKey(String langKey) {
    this.langKey = langKey;
  }

  @Override
  public String toString() {
    return "SignupRequest{" +
        "username='" + username + '\'' +
        ", email='" + email + '\'' +
        ", displayName='" + displayName + '\'' +
        ", preferredName='" + preferredName + '\'' +
        ", langKey='" + langKey + '\'' +
        '}';
  }
}