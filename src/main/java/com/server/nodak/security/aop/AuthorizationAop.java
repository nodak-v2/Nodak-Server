package com.server.nodak.security.aop;

import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.exception.common.AuthorizationException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

@Aspect
@Component
public class AuthorizationAop {

    @Before("@annotation(com.server.nodak.security.aop.AuthorizationRequired)")
    public void checkAccessLevel(JoinPoint joinPoint) {
        AuthorizationRequired annotation = getAnnotation(joinPoint);

        Collection<? extends GrantedAuthority> possibleAuthority = roleToAuthority(annotation.value());

        if (!hasAuthority(possibleAuthority)) {
            throw new AuthorizationException();
        }
    }

    private boolean hasAuthority(Collection<? extends GrantedAuthority> possibleAuthority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities()
                .stream().anyMatch(possibleAuthority::contains);
    }

    private AuthorizationRequired getAnnotation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        return method.getAnnotation(AuthorizationRequired.class);
    }

    private Collection<? extends GrantedAuthority> roleToAuthority(UserRole[] required) {
        return Arrays.stream(required)
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
