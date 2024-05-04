package com.server.nodak.global.config;

//@OpenAPIDefinition(info = @Info(title = "Nodak API 명세서", description = "Nodak API 명세서", version = "v1"))
//@Configuration
//@EnableWebMvc
//@Import({SpringDocConfiguration.class,
//        SpringDocWebMvcConfiguration.class,
//        org.springdoc.webmvc.ui.SwaggerConfig.class,
//        SwaggerUiConfigProperties.class,
//        SwaggerUiOAuthProperties.class,
//        JacksonAutoConfiguration.class,
//        SpringDocConfigProperties.class})
//public class SwaggerConfig {
//
//    private final static String BEARER_TOKEN_PREFIX = "Bearer";
//
//    @Bean
//    OpenAPI openAPI() {
//        String schemeName = "Authorization";
//        SecurityRequirement securityRequirement = new SecurityRequirement().addList(schemeName);
//        Components components = new Components();
//        components.addSecuritySchemes(
//                schemeName, new SecurityScheme()
//                        .name(schemeName)
//                        .type(Type.HTTP)
//                        .scheme(BEARER_TOKEN_PREFIX)
//                        .bearerFormat("JWT")
//        );
//        return new OpenAPI()
//                .addSecurityItem(securityRequirement)
//                .components(components);
//    }
//}