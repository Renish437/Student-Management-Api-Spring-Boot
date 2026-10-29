package com.renish.backend.config;

import com.renish.backend.swagger.SwaggerTags;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

/**
 * Global Swagger/OpenAPI definition: API info banner, server list, tag
 * catalogue and the JWT security scheme.
 *
 * Tag names/descriptions come from {@link SwaggerTags} so the docs header and
 * the controllers always agree; edit text there or in {@code SwaggerOperations}
 * / {@code SwaggerResponses} and Swagger UI follows automatically.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "EduTrack - Student Management System API",
                version = "1.0.0",
                description = """
                        RESTful API for **EduTrack**, a Student Management System covering student registration,
                        course and department management, enrolment, dynamic filtering and reporting, secured with
                        JWT authentication and role-based access control.

                        **Base path:** `/api/v1`
                        **Documentation**
                        - Swagger UI: `/swagger-ui.html`
                        - OpenAPI JSON: `/v3/api-docs`
                        """,
                contact = @Contact(
                        name = "Renish Siwakoti",
                        email = "renishsiwakoti437@gmail.com",
                        url = "https://github.com/Renish437"
                )
        ),
        servers = @Server(url = "/", description = "Current server"),
        tags = {
                @Tag(name = SwaggerTags.HOME, description = SwaggerTags.HOME_DESCRIPTION),
                @Tag(name = SwaggerTags.AUTH, description = SwaggerTags.AUTH_DESCRIPTION),
                @Tag(name = SwaggerTags.STUDENTS, description = SwaggerTags.STUDENTS_DESCRIPTION),
                @Tag(name = SwaggerTags.COURSES, description = SwaggerTags.COURSES_DESCRIPTION),
                @Tag(name = SwaggerTags.DEPARTMENTS, description = SwaggerTags.DEPARTMENTS_DESCRIPTION),
                @Tag(name = SwaggerTags.ADMIN_STUDENTS, description = SwaggerTags.ADMIN_STUDENTS_DESCRIPTION),
                @Tag(name = SwaggerTags.ADMIN_COURSES, description = SwaggerTags.ADMIN_COURSES_DESCRIPTION),
                @Tag(name = SwaggerTags.ADMIN_DEPARTMENTS, description = SwaggerTags.ADMIN_DEPARTMENTS_DESCRIPTION),
                @Tag(name = SwaggerTags.OTP, description = SwaggerTags.OTP_DESCRIPTION)
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type= SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenAPIConfig {
}
