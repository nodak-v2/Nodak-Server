package com.server.nodak.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("spring.jwt")
public class JwtProperties {
    private String key;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;
}
