package com.rapidobackup.console.config;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Implementation of AuditorAware to provide the current user's UUID for JPA auditing.
 * Extracts the user UUID from the JWT token subject stored in SecurityContext.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<UUID> {

    @Override
    public  @NonNull Optional<UUID> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getPrincipal)
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .flatMap(this::parseUUID);
    }
    
    private Optional<UUID> parseUUID(String uuidString) {
        try {
            return Optional.of(UUID.fromString(uuidString));
        } catch (IllegalArgumentException e) {
            // Log warning and return empty if UUID parsing fails
            return Optional.empty();
        }
    }
}