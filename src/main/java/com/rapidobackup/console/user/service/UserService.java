package com.rapidobackup.console.user.service;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rapidobackup.console.user.dto.UserDto;
import com.rapidobackup.console.user.entity.User;
import com.rapidobackup.console.user.entity.UserRole;
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
    dto.setLogin(user.getLogin());
    dto.setFirstName(user.getFirstName());
    dto.setLastName(user.getLastName());
    dto.setEmail(user.getEmail());
    dto.setActivated(user.isActivated());
    dto.setLangKey(user.getLangKey());
    dto.setImageUrl(user.getImageUrl());
    dto.setRole(user.getRole());
    dto.setCreatedDate(user.getCreatedDate());
    dto.setLastModifiedDate(user.getLastModifiedDate());
    dto.setLastLogin(user.getLastLogin());

    if (user.getParent() != null) {
      dto.setParentId(user.getParent().getId());
      dto.setParentLogin(user.getParent().getLogin());
    }

    return dto;
  }

  public User createUser(String login, String email, String password, String firstName, String lastName, String langKey) {
    User user = new User();
    user.setLogin(login);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setLangKey(langKey != null ? langKey : "en");
    user.setRole(UserRole.CLIENT);
    user.setActivated(true);
    user.setCreatedBy("system");
    user.setCreatedDate(Instant.now());
    user.setLastModifiedBy("system");
    user.setLastModifiedDate(Instant.now());
    
    return userRepository.save(user);
  }
}