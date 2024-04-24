package com.server.nodak.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class TokenProvider {
    private final AtomicLong atomicLong = new AtomicLong(1L);
    private final JwtProperties jwtProperties;
    private Key key;

    public TokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getKey().getBytes());
    }

    public String createAccessToken(String subject) {
        return createToken(subject, jwtProperties.getAccessTokenExpiration());
    }

    public String createRefreshToken(String subject) {
        return createToken(subject, jwtProperties.getRefreshTokenExpiration());
    }

    public boolean validateToken(String token) {
        try {
            getClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getSubject(String token) {
        Claims body = getClaimsJws(token).getBody();
        return body.getSubject();
    }

    private Jws<Claims> getClaimsJws(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    private String createToken(String subject, long millis) {
        Claims claims = Jwts.claims().setSubject(subject);
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + millis + getRandomSeconds());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .setSubject(subject)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private long getRandomSeconds() {
        return (atomicLong.incrementAndGet() % 10) * 1000;
    }
}
