package com.renish.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AppConfig {

    @Bean
    public String appName() {
        log.info("AppConfig: appName bean created");
        return "Spring Boot Application- Student Management System";
    }

//    private static final String SECURITY_SCHEME_NAME = "bearerAuth";
//
//    @Bean
//    public OpenAPI openAPI() {
//        return new OpenAPI()
//                .info(new Info()
//                        .title("EduTrack - Student Management System API")
//                        .description(
//                                "RESTful API for EduTrack - A comprehensive Student Management System designed to streamline student registration, academic records, course enrollment, attendance tracking, and student performance management with secure JWT-based authentication and role-based access control.")
//                        .version("1.0.0")
//                        .contact(new Contact()
//                                .name("Renish Hero")
//                                .email("renish.hero@example.com")
//                                .url("https://github.com/renishhero")))
//                .components(new Components()
//                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
//                                new SecurityScheme()
//                                        .type(SecurityScheme.Type.HTTP)
//                                        .scheme("bearer")
//                                        .bearerFormat("JWT")));
//    }

}
