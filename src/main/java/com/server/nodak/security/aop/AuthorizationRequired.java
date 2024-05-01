package com.server.nodak.security.aop;

import com.server.nodak.domain.user.domain.UserRole;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Retention(RUNTIME)
@Target(METHOD)
public @interface AuthorizationRequired {
    UserRole[] value();
}
