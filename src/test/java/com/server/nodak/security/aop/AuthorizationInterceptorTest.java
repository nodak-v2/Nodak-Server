package com.server.nodak.security.aop;

import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.exception.common.AuthorizationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;

import static com.server.nodak.domain.user.domain.UserRole.GENERAL;
import static com.server.nodak.domain.user.domain.UserRole.MANAGER;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthorizationInterceptorTest {
    @Autowired
    AuthorizationInterceptor interceptor;

    @Test
    @DisplayName("어노테이션이 없으면 정상적으로 인터셉터를 통과해야한다.")
    void notHaveAnnotation() throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HandlerMethod handlerMethod = mock(HandlerMethod.class);

        // when
        when(handlerMethod.getMethodAnnotation(AuthorizationRequired.class))
                .thenReturn(null);

        // then
        interceptor.preHandle(request, response, handlerMethod);
    }

    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("접근 권한이 없다면 예외가 반한된다.")
    void notHaveAccessRight(UserRole currentRole) {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        AuthorizationRequired annotation = mock(AuthorizationRequired.class);
        setAuthority(currentRole);

        // when
        when(annotation.value())
                .thenReturn(exceptRole(currentRole));
        when(handlerMethod.getMethodAnnotation(AuthorizationRequired.class))
                .thenReturn(annotation);
        when(annotation.status())
                .thenReturn(HttpStatus.UNAUTHORIZED);

        // then
        if (currentRole != MANAGER) {
            assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod))
                    .isInstanceOf(AuthorizationException.class);
        }

        if (currentRole != GENERAL) {
            assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod))
                    .isInstanceOf(AuthorizationException.class);
        }
    }

    private void setAuthority(UserRole role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                null, null, singleton(new SimpleGrantedAuthority(role.name()))
        ));
    }

    private UserRole[] exceptRole(UserRole except) {
        return Arrays.stream(UserRole.values())
                .filter(a -> a != except)
                .toArray(UserRole[]::new);
    }
}