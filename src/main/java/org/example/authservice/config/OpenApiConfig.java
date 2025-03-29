package org.example.authservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition (
        servers = {@Server(url = "31064:/auth/", description = "Default server url"), @Server(url = "/", description = "localhost")},
        info = @Info(
        title = "Authentication module of task management platform",
        description = "Made as research project at TPU", version = "1.0.0",
        contact = @Contact(
                name = "Михалёв Максим",
                url = "https://t.me/Handlest"
        )
    )
)
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer")
public class OpenApiConfig {

}