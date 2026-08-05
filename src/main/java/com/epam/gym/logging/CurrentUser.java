package com.epam.gym.logging;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

final class CurrentUser {

    private static final String ANONYMOUS = "anonymous";

    static String username() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : ANONYMOUS;
    }

    private CurrentUser() {
    }
}
