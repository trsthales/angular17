package com.example.ecommerce.boot.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração OpenAPI/Swagger para a aplicação.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Adiciona esquema de segurança Bearer (JWT) para a UI do Swagger
        SecurityScheme bearerScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("Use 'Bearer <token>' obtido via POST /dev/token (ambiente de desenvolvimento). Example: 'Bearer <JWT>'");

        return new OpenAPI()
            .components(new Components().addSecuritySchemes("bearerAuth", bearerScheme))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .info(new Info()
                .title("Ecommerce API")
                .version("0.1.0")
                .description("API pública para o exemplo de e-commerce")
                .contact(new Contact().name("Equipe de Desenvolvimento")));
    }
}
