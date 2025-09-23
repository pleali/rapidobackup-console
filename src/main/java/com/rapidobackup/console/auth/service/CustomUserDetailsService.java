package com.rapidobackup.console.auth.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rapidobackup.console.auth.principal.CustomUserPrincipal;
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
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("User account is not activated: " + username);
        }

        if (user.isAccountLocked()) {
            throw new UsernameNotFoundException("User account is locked: " + username);
        }

        return new CustomUserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                    .collect(java.util.stream.Collectors.toList()),
                true, // accountNonExpired
                !user.isAccountLocked(), // accountNonLocked
                true, // credentialsNonExpired
                user.isActive() // enabled
        );
    }
}