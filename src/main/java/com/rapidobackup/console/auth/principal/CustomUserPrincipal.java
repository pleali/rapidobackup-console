package com.rapidobackup.console.auth.principal;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Custom implementation of Spring Security's UserDetails that stores the user's UUID
 * as the primary identifier instead of the username.
 *
 * This design facilitates future migration to JWT tokens where the UUID will be stored
 * in the token subject, providing consistency across authentication mechanisms.
 */
public class CustomUserPrincipal implements UserDetails {

    private final UUID userId;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;

    public CustomUserPrincipal(UUID userId, String username, String password,
                              Collection<? extends GrantedAuthority> authorities,
                              boolean accountNonExpired, boolean accountNonLocked,
                              boolean credentialsNonExpired, boolean enabled) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    /**
     * Returns the user's UUID as a string.
     * This method returns the UUID instead of the username to prepare for JWT migration.
     *
     * @return the user's UUID as a string
     */
    public String getName() {
        return userId.toString();
    }

    /**
     * Returns the user's UUID.
     *
     * @return the user's UUID
     */
    public UUID getUserId() {
        return userId;
    }

    /**
     * Returns the username for display purposes.
     * Note: This is NOT used as the principal identifier.
     *
     * @return the username
     */
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "CustomUserPrincipal{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}