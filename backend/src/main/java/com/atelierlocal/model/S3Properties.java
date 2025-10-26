package com.atelierlocal.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Classe de configuration pour les propriétés AWS S3.
 * 
 * Cette classe permet de centraliser les informations nécessaires pour
 * se connecter à un bucket S3 :
 * - nom du bucket
 * - région AWS
 * - clé d'accès
 * - clé secrète
 * 
 * Les valeurs sont injectées depuis le fichier de configuration (application.properties ou application.yml)
 * grâce au préfixe "aws.s3".
 */
@ConfigurationProperties(prefix = "aws.s3")
@Component
public class S3Properties {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /**
     * Nom du bucket S3.
     */
    private String bucketName;

    /**
     * Région AWS du bucket.
     */
    private String region;

    /**
     * Clé d'accès AWS.
     */
    private String accessKey;

    /**
     * Clé secrète AWS.
     */
    private String secretKey;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public String getBucketName() { return bucketName; }
    public void setBucketName(String bucketName) { this.bucketName = bucketName; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }

    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
}
