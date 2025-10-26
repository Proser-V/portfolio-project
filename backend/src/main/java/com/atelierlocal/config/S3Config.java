package com.atelierlocal.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.atelierlocal.model.S3Properties;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Classe de configuration pour AWS S3.
 * Cette classe configure un client S3 (S3Client) pour interagir avec le service S3 d'AWS.
 * Les propriétés de connexion (clé, secret, région) sont injectées via la classe S3Properties.
 */
@Configuration
@EnableConfigurationProperties(S3Properties.class) // Permet de lier la configuration S3Properties aux propriétés Spring
public class S3Config {

    /**
     * Bean qui crée et configure un client AWS S3.
     * 
     * @param s3Properties objet contenant les informations de connexion (region, accessKey, secretKey)
     * @return un S3Client prêt à être utilisé pour les opérations S3
     */
    @Bean
    public S3Client s3Client(S3Properties s3Properties) {

        // DEBUG : affichage des informations de configuration pour vérification
        System.out.println("DEBUG - Region: '" + s3Properties.getRegion() + "'");
        System.out.println("DEBUG - AccessKey: '" + s3Properties.getAccessKey() + "'");

        // Vérification de la configuration obligatoire : la région AWS ne doit pas être vide
        if (s3Properties.getRegion() == null || s3Properties.getRegion().isEmpty()) {
            throw new IllegalStateException("AWS S3 region is not configured!");
        }

        // Construction et retour du client S3
        return S3Client.builder()
                // Configuration de la région AWS
                .region(Region.of(s3Properties.getRegion()))
                // Configuration des identifiants statiques pour l'authentification
                .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        s3Properties.getAccessKey(),   // Clé d'accès AWS
                        s3Properties.getSecretKey()    // Clé secrète AWS
                    )
                ))
                .build(); // Création finale du client S3
    }
}
