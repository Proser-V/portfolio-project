package com.atelierlocal;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Point d'entrée principal de l'application Spring Boot.
 * 
 * Cette classe initialise le contexte Spring, configure JPA et définit
 * le fuseau horaire par défaut de l'application.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.atelierlocal.repository")
public class BackendApplication {

    /**
     * Méthode principale exécutée au démarrage de l'application.
     * 
     * - Définit le fuseau horaire par défaut sur UTC afin d'assurer 
     *   une cohérence dans la gestion des dates et heures entre le serveur et les clients.
     * - Lance l'application Spring Boot à l'aide de {@link SpringApplication#run}.
     * 
     * @param args arguments de la ligne de commande (non utilisés ici)
     */
    public static void main(String[] args) {
        // Configuration du fuseau horaire global de l'application
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        
        // Démarrage du contexte Spring Boot
        SpringApplication.run(BackendApplication.class, args);
    }
}
