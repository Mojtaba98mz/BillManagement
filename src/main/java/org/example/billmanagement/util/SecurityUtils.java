package org.example.billmanagement.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    /**
     * Retrieves the current authenticated user.
     *
     * @return the UserDetails of the current authenticated user, or null if no user is authenticated.
     */
    public UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        return null; // No user is authenticated
    }

    /**
     * Retrieves the username of the current authenticated user.
     *
     * @return the username of the current authenticated user, or null if no user is authenticated.
     */
    public String getCurrentUsername() {
        UserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getUsername() : null;
    }
}