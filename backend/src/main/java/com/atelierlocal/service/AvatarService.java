package com.atelierlocal.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class AvatarService {
    private final S3Client s3Client;
    private final String bucketName = "atelierlocal-bucket1";

    public AvatarService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadAvatar(MultipartFile file, UUID userId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier fourni");
        }

        // Vérification du type
        List<String> allowedTypes = List.of("image/png", "image/jpeg");
        if (!allowedTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("Type de fichier non autorisé. Seuls PNG et JPEG sont acceptés.");
        }

        // Vérification de la taille (max 5 Mo)
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("Fichier trop volumineux. Maximum autorisé : 5 Mo");
        }

        try {
            // Nom du ficher dans S3
            String key = "avatars/" + userId + "/" + file.getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .acl("public-read") // rend l'objet accessible publiquement
                .contentType(file.getContentType())
                .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucketName,
                    s3Client.serviceClientConfiguration().region().id(),
                    key
            );
        } catch (IOException err) {
            err.printStackTrace();
            throw new RuntimeException("Erreur lors de l'upload de l'avatar", err);
        }

    }

    public String getFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (filename == null || !filename.contains(".")) { return ""; }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
