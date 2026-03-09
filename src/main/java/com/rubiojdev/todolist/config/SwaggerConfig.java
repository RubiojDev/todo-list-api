package com.rubiojdev.todolist.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.http.HttpHeaders;

@OpenAPIDefinition(
        info = @Info(
                title = "API TODO LIST",
                description = "Api que sirve para crear distintas tareas con sus respectivos items",
                contact = @Contact(
                        name = "Jesus Rubio",
                        url = "MiPagina/Contact",
                        email = "jesusantoniorubiot@gmail.com"
                ),
                version = "1.0.0",
                termsOfService = "MiPagina/terminos_y_servicios",
                license = @License(
                        name = "Standar Software Use License for MiPagina",
                        url = "MiPagina/license"
                )
        ),
        servers = {
                @Server(
                        description = "DEV SERVER",
                        url = "http://localhost:8080/api"
                ),
                @Server(
                        description = "PROD SERVER",
                        url = "URL-DE-PRODUCCION:8080/api"
                )
        },
        security = @SecurityRequirement(
                name = "Security Token"
        )
)
@SecurityScheme(
        name = "Security Token",
        description = "Access Token For My API",
        type = SecuritySchemeType.HTTP,
        paramName = HttpHeaders.AUTHORIZATION,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {
}
