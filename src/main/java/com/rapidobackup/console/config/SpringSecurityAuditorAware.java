package com.rapidobackup.console.config;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.rapidobackup.console.auth.principal.CustomUserPrincipal;

/**
 * Implementation of AuditorAware to provide the current user's UUID for JPA auditing.
 * Extracts the user UUID from the CustomUserPrincipal stored in SecurityContext.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<UUID> {

    @Override
    public  @NonNull Optional<UUID> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getPrincipal)
            .filter(CustomUserPrincipal.class::isInstance)
            .map(CustomUserPrincipal.class::cast)
            .map(CustomUserPrincipal::getUserId);
    }
}