package ru.beeline.techradar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("ru.beeline.techradar.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(Arrays.asList(
                        new RequestParameterBuilder()
                                .name("user-roles")
                                .description("User roles header")
                                .required(true)
                                .in("header")
                                .build()
                ));
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                "Eafdmmart API",
                "API документация",
                "1.0",
                "Terms of service",
                new Contact("Example", "www.example.com", "example@company.com"),
                "License of API", "API license URL", Collections.emptyList());
    }
}
