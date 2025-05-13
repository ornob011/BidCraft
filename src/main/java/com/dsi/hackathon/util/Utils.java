package com.dsi.hackathon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class Utils {
    private Utils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static boolean isLoggedIn(Authentication authentication) {
        return authentication != null
               && !(authentication instanceof AnonymousAuthenticationToken)
               && authentication.isAuthenticated();
    }

    public static boolean isLoggedIn() {
        return isLoggedIn(getAuthentication());
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
