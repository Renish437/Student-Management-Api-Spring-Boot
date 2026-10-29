package com.renish.backend.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component("auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * Audit fields (createdBy/lastModifiedBy and the soft-delete markers) record the
     * authenticated caller. Outside a request - application startup, schedulers, tests
     * calling services directly - there is no authentication, so those callers fall
     * back to their own default ("admin") instead of writing a bogus user.
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return Optional.of(userDetails.getUsername());
        }

        String name = authentication.getName();
        if (name == null || name.isBlank() || "anonymousUser".equals(name)) {
            return Optional.empty();
        }
        return Optional.of(name);
    }
}
