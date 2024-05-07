package com.server.nodak.domain.s3.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;

@Documented
@Target({ PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { PathValidator.class })
public @interface ValidateImagePath {

    Class<? extends Enum<?>> enumClass();
    String message() default "올바른 s3 경로가 아닙니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean ignoreCase() default true;
}