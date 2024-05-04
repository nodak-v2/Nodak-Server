package com.server.nodak.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@OpenAPIDefinition(info = @Info(title = "Nodak API 명세서", description = "Nodak API 명세서", version = "v1"))
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.server.nodak"})
@Import({SpringDocConfiguration.class,
        SpringDocWebMvcConfiguration.class,
        org.springdoc.webmvc.ui.SwaggerConfig.class,
        SwaggerUiConfigProperties.class,
        SwaggerUiOAuthProperties.class,
        JacksonAutoConfiguration.class,
        SpringDocConfigProperties.class})
public class SwaggerConfig {

    private final static String BEARER_TOKEN_PREFIX = "Bearer";

    @Bean
    OpenAPI openAPI() {
        String schemeName = "Authorization";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(schemeName);
        Components components = new Components();
        components.addSecuritySchemes(
                schemeName, new SecurityScheme()
                        .name(schemeName)
                        .type(Type.HTTP)
                        .scheme(BEARER_TOKEN_PREFIX)
                        .bearerFormat("JWT")
        );
        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}