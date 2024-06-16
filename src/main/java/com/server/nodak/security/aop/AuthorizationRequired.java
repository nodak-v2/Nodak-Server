package com.server.nodak.security.aop;

import com.server.nodak.domain.user.domain.UserRole;
import org.springframework.http.HttpStatus;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Retention(RUNTIME)
@Target(METHOD)
public @interface AuthorizationRequired {
    UserRole[] value();

    String failureMessage() default "";

    HttpStatus status() default HttpStatus.UNAUTHORIZED;
}
