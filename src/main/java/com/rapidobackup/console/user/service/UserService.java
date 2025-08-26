package com.rapidobackup.console.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rapidobackup.console.user.dto.UserDto;
import com.rapidobackup.console.user.entity.User;
import com.rapidobackup.console.user.repository.UserRepository;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
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
}