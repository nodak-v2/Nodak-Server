package com.server.nodak.security.aop;

import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.exception.common.AuthorizationException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.server.nodak.domain.user.domain.UserRole.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(AuthorizationAopTest.AnnotationAopTester.class)
class AuthorizationAopTest {
    @Autowired
    AuthorizationAop authorizationAop;
    @Autowired
    AnnotationAopTester annotationAopTester;

    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("접근 권한이 없는 컨트롤러에 접근 시 예외가 반한된다.")
    void notHaveAccessRight(UserRole currentRole) {
        // given
        setAuthority(currentRole);

        // when

        // then
        if (currentRole != MANAGER) {
            assertThatThrownBy(() -> annotationAopTester.managerRoleFunc())
                    .isInstanceOf(AuthorizationException.class);
        }

        if (currentRole != GENERAL) {
            assertThatThrownBy(() -> annotationAopTester.generalRoleFunc())
                    .isInstanceOf(AuthorizationException.class);
        }
    }

    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("접근 권한이 없는 컨트롤러에 접근 시 예외가 반한된다.")
    void hasAccessRight(UserRole currentRole) {
        // given
        setAuthority(currentRole);

        // when

        // then
        if (currentRole == MANAGER) {
            assertTrue(annotationAopTester.managerRoleFunc());
        }

        if (currentRole == GENERAL) {
            assertTrue(annotationAopTester.generalRoleFunc());
        }
    }

    private void setAuthority(UserRole role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                null, null, singleton(new SimpleGrantedAuthority(role.name()))
        ));
    }

    @Component
    static class AnnotationAopTester {

        @AuthorizationRequired(GENERAL)
        public boolean generalRoleFunc() {
            return true;
        }

        @AuthorizationRequired(MANAGER)
        public boolean managerRoleFunc() {
            return true;
        }

    }

}