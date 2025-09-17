package com.rapidobackup.console.auth.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rapidobackup.console.user.entity.User;
import com.rapidobackup.console.user.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!user.isActivated()) {
            throw new UsernameNotFoundException("User account is not activated: " + username);
        }

        if (user.isAccountLocked()) {
            throw new UsernameNotFoundException("User account is locked: " + username);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .accountExpired(false)
                .accountLocked(user.isAccountLocked())
                .credentialsExpired(false)
                .disabled(!user.isActivated())
                .build();
    }
}