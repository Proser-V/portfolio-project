package com.atelierlocal.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.atelierlocal.model.S3Properties;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;


@Configuration
@EnableConfigurationProperties(S3Properties.class)
public class S3Config {

    @Bean
    public S3Client s3Client(S3Properties s3Properties) {
    System.out.println("DEBUG - Region: '" + s3Properties.getRegion() + "'");
    System.out.println("DEBUG - AccessKey: '" + s3Properties.getAccessKey() + "'");
    
    // Vérification
    if (s3Properties.getRegion() == null || s3Properties.getRegion().isEmpty()) {
        throw new IllegalStateException("AWS S3 region is not configured!");
    }
        return S3Client.builder()
                .region(Region.of(s3Properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        s3Properties.getAccessKey(),
                        s3Properties.getSecretKey()
                    )
                ))
                .build();
    }
}
