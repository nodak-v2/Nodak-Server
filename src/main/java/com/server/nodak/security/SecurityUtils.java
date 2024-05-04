package com.server.nodak.security;

import com.server.nodak.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityUtils {

    public static boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }

    public static Long getUserId() {
        return SecurityContextHolder.getContext().getAuthentication() != null ?
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId() :
                null;
    }
}
