package com.rapidobackup.console.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

  @NotBlank
  @Size(min = 3, max = 50)
  private String login;

  @NotBlank
  @Size(min = 4, max = 100)
  private String password;

  private boolean rememberMe = false;

  public LoginRequest() {}

  public LoginRequest(String login, String password) {
    this.login = login;
    this.password = password;
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

  public boolean isRememberMe() {
    return rememberMe;
  }

  public void setRememberMe(boolean rememberMe) {
    this.rememberMe = rememberMe;
  }

  @Override
  public String toString() {
    return "LoginRequest{" + "login='" + login + '\'' + ", rememberMe=" + rememberMe + '}';
  }
}