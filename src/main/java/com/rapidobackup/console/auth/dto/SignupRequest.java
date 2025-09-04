package com.rapidobackup.console.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {

  @NotBlank
  @Size(min = 3, max = 50)
  private String login;

  @NotBlank
  @Email
  @Size(min = 5, max = 100)
  private String email;

  @NotBlank
  @Size(min = 6, max = 100)
  private String password;

  @Size(max = 50)
  private String firstName;

  @Size(max = 50)
  private String lastName;

  @Size(min = 2, max = 10)
  private String langKey = "en";

  public SignupRequest() {}

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
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

  public String getLangKey() {
    return langKey;
  }

  public void setLangKey(String langKey) {
    this.langKey = langKey;
  }

  @Override
  public String toString() {
    return "SignupRequest{" +
        "login='" + login + '\'' +
        ", email='" + email + '\'' +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", langKey='" + langKey + '\'' +
        '}';
  }
}