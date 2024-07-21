package com.server.nodak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOriginPatterns("*") // 안에 해당 주소를 넣어도 됨
                    .allowedHeaders("*")
                    .allowedMethods("*")
                    .exposedHeaders("*");
                //.allowCredentials(true);
            }
        };
    }

}
