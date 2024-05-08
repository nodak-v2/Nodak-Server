package com.server.nodak.security.aop;

import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.exception.common.AuthorizationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Collection;

public class AuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AuthorizationRequired annotation = getAnnotation(handler);

        if (annotation == null) {
            return true;
        }

        Collection<? extends GrantedAuthority> possibleAuthority = roleToAuthority(annotation.value());

        if (!hasAuthority(possibleAuthority)) {
            throw new AuthorizationException();
        }

        return true;
    }

    private boolean hasAuthority(Collection<? extends GrantedAuthority> possibleAuthority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean b = authorities
                .stream().anyMatch(possibleAuthority::contains);
        return b;
    }

    private AuthorizationRequired getAnnotation(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        return handlerMethod.getMethodAnnotation(AuthorizationRequired.class);
    }

    private Collection<? extends GrantedAuthority> roleToAuthority(UserRole[] required) {
        return Arrays.stream(required)
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
