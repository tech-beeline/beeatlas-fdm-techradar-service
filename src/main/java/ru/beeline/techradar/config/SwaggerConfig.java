package ru.beeline.techradar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ru.beeline.techradar.utils.Constant.*;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .securitySchemes(List.of(apiKey()))
                .securityContexts(List.of(securityContext()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("ru.beeline.techradar.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(Arrays.asList(
                        new RequestParameterBuilder()
                                .name(USER_ROLES_HEADER)
                                .description("Роли пользователя")
                                .required(false)
                                .in("header")
                                .build(),
                        new RequestParameterBuilder()
                                .name(USER_ID_HEADER)
                                .description("ID пользователя")
                                .required(false)
                                .in("header")
                                .build(),
                        new RequestParameterBuilder()
                                .name(USER_PERMISSION_HEADER)
                                .description("Права пользователя")
                                .required(false)
                                .in("header")
                                .build(),
                        new RequestParameterBuilder()
                                .name(USER_PRODUCTS_IDS_HEADER)
                                .description("ID продуктов пользователя")
                                .required(false)
                                .in("header")
                                .build()
                ));
    }

    private ApiKey apiKey() {
        return new ApiKey("Bearer", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[]{authorizationScope};
        return List.of(new SecurityReference("Bearer", authorizationScopes));
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                "Tehradar API",
                "API документация",
                "1.0",
                "Terms of service",
                new Contact("Example", "www.example.com", "example@company.com"),
                "License of API", "API license URL", Collections.emptyList());
    }
}
