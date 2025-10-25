package com.atelierlocal.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuration pour Swagger / OpenAPI.
 * Cette classe définit la documentation automatique de l'API
 * et configure l'authentification via JWT (Bearer Token) pour Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Bean principal pour configurer OpenAPI.
     * 
     * @return un objet OpenAPI configuré avec les informations générales de l'API
     *         et la sécurité JWT.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // Nom utilisé pour référencer le schéma de sécurité
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
            // Informations générales sur l'API affichées dans Swagger UI
            .info(new Info()
                .title("Atelier Local API")             // Titre de l'API
                .version("1.0")                          // Version de l'API
                .description("Documentation de l'API de L'Atelier Local") // Description
            )
            // Définition de la sécurité globale appliquée à toutes les routes
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            // Composants supplémentaires pour OpenAPI
            .components(
                new Components()
                    // Définition du schéma de sécurité JWT
                    .addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                            .name(securitySchemeName)       // Nom du schéma
                            .type(SecurityScheme.Type.HTTP) // Type HTTP pour Bearer token
                            .scheme("bearer")               // Schéma d'authentification
                            .bearerFormat("JWT")            // Format du token
                    )
            );
    }
}
