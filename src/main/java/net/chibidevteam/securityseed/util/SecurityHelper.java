package net.chibidevteam.securityseed.util;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityHelper {

    private SecurityHelper() {
    }

    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return auth.isAuthenticated();
    }

}
