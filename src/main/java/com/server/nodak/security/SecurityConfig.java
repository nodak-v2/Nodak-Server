package com.server.nodak.security;

import com.server.nodak.security.jwt.JwtFilter;
import com.server.nodak.security.oauth.OAuthServiceHandler;
import com.server.nodak.security.oauth.handler.OAuthAuthenticationFailureHandler;
import com.server.nodak.security.oauth.handler.OAuthAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuthAuthenticationSuccessHandler oAuthSuccessHandler;
    private final OAuthAuthenticationFailureHandler oAuthFailureHandler;
    private final OAuthServiceHandler oAuthServiceHandler;
    private final JwtFilter jwtFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .oauth2Login(configurer ->
                        configurer.authorizationEndpoint(endpoint ->
                                        endpoint.baseUri("/oauth2/authorization")
                                )
                                .userInfoEndpoint(customizer ->
                                        customizer.userService(oAuthServiceHandler)
                                )
                                .successHandler(oAuthSuccessHandler)
                                .failureHandler(oAuthFailureHandler)
                )
                .authorizeHttpRequests(request ->
                        request.requestMatchers("/**").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(configurer ->
                        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
