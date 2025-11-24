package com.rairai.rairai_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    OpenAPI configurarSwagger() {
        return new OpenAPI()
            .info(
                new Info()
                    .title("RaiRai API - Sistema de Cadastro de Usuários")
                    .description(
                        "Bem-vindo à RaiRai API! 🚀 " +
                            "Uma API robusta e segura para gerenciamento completo de usuários. " +
                            "Realize cadastros, atualizações, consultas e exclusões de forma simples e eficiente. " +
                            "Desenvolvido com Spring Boot seguindo as melhores práticas de desenvolvimento! ❤️"
                    )
                    .summary(
                        "API REST completa para cadastro e gerenciamento de usuários. " +
                            "Inclui autenticação JWT, endpoints seguros e documentação interativa."
                    )
                    .version("v1.0.0")
                    .license(
                        new License()
                            .url("https://github.com/gsnimbus/java")
                            .name("MIT License - GsNimbus © 2024")
                    )
                    .termsOfService(
                        "https://rairai.com/terms - Use com responsabilidade e cuide da sua saúde! 💪"
                    )
            )
            .addSecurityItem(
                new SecurityRequirement().addList("Bearer Authentication")
            )
            .components(
                new Components().addSecuritySchemes(
                    "Bearer Authentication",
                    createAPIKeyScheme()
                )
            );
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .bearerFormat("JWT")
            .scheme("bearer");
    }
}
