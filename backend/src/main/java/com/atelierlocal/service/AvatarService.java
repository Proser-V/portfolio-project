package com.atelierlocal.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Service pour la gestion des avatars des utilisateurs.
 * 
 * Fournit des méthodes pour :
 * - téléverser un avatar sur AWS S3,
 * - récupérer l'extension d'un fichier avatar.
 */
@Service
public class AvatarService {
    private final S3Client s3Client;
    private final String bucketName = "atelierlocal-bucket1";

    public AvatarService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Upload un fichier avatar vers S3 pour un utilisateur donné.
     * 
     * @param file fichier avatar à uploader
     * @param userId identifiant de l'utilisateur (pour structurer le chemin dans S3)
     * @return URL publique de l'avatar
     * @throws IllegalArgumentException si le fichier est vide, non autorisé ou trop volumineux
     * @throws RuntimeException si une erreur survient lors de l'upload
     */
    public String uploadAvatar(MultipartFile file, UUID userId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier fourni");
        }

        // Vérification du type de fichier autorisé (PNG et JPEG)
        List<String> allowedTypes = List.of("image/png", "image/jpeg");
        if (!allowedTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("Type de fichier non autorisé. Seuls PNG et JPEG sont acceptés.");
        }

        // Vérification de la taille maximale (5 Mo)
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("Fichier trop volumineux. Maximum autorisé : 5 Mo");
        }

        try {
            // Génération du chemin du fichier dans S3
            String key = "avatars/" + userId + "/" + file.getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .acl("public-read") // rendre le fichier accessible publiquement
                .contentType(file.getContentType())
                .build();

            // Upload du fichier
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            // Retourne l'URL publique de l'avatar
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

    /**
     * Récupère l'extension du fichier avatar.
     * 
     * @param file fichier avatar
     * @return extension du fichier (ex: "png", "jpg") ou chaîne vide si non trouvée
     */
    public String getFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (filename == null || !filename.contains(".")) { return ""; }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
