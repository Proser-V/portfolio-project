package com.atelierlocal.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.UploadedPhoto;
import com.atelierlocal.model.User;
import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.security.SecurityService;

import jakarta.persistence.EntityNotFoundException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

/**
 * Service de gestion du portfolio des artisans.
 * 
 * Fournit les fonctionnalités suivantes :
 * - Upload d'images dans le portfolio sur AWS S3
 * - Création et liaison des photos avec l'artisan
 * - Suppression de photos
 * - Récupération de l'ensemble du portfolio d'un artisan
 */
@Service
public class PortfolioService {

    private final S3Client s3Client;
    private final ArtisanRepo artisanRepo;
    private final SecurityService securityService;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public PortfolioService(S3Client s3Client, ArtisanRepo artisanRepo, SecurityService securityService) {
        this.s3Client = s3Client;
        this.artisanRepo = artisanRepo;
        this.securityService = securityService;
    }

    // ================= Upload + création photo =================

    /**
     * Ajoute une photo au portfolio d'un artisan.
     * 
     * Vérifie les droits de l'utilisateur, la validité du fichier, effectue l'upload sur S3,
     * crée l'entité UploadedPhoto et l'associe à l'artisan.
     *
     * @param artisanId ID de l'artisan
     * @param file fichier à uploader
     * @param currentArtisan artisan courant (pour vérification des droits)
     * @return UploadedPhoto créée et persistée
     */
    public UploadedPhoto addPhoto(UUID artisanId, MultipartFile file, Artisan currentArtisan) {
        // Vérification que seul un artisan peut uploader dans son portfolio
        securityService.checkArtisanOnly(currentArtisan);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier fourni.");
        }

        // Vérification du type de fichier autorisé (PNG / JPEG)
        List<String> allowedTypes = List.of("image/png", "image/jpeg");
        if (!allowedTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("Type de fichier non autorisé. Seuls PNG et JPEG sont acceptés.");
        }

        // Vérification de la taille du fichier (max 5 Mo)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Fichier trop volumineux. Maximum 5 Mo.");
        }

        // Récupération de l'artisan depuis la base de données
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new EntityNotFoundException("Professionnel non trouvé."));

        // Construction de la clé S3 et préparation de la requête d'upload
        String key = "portfolio/" + artisanId + "/" + file.getOriginalFilename();
        PutObjectRequest putReq = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .acl("public-read") // rendre l'image accessible publiquement
            .contentType(file.getContentType())
            .build();

        // Upload sur S3
        try {
            s3Client.putObject(putReq, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new RuntimeException("Erreur upload S3", e);
        }

        // Construction de l'URL publique de la photo
        String publicUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
            bucketName,
            s3Client.serviceClientConfiguration().region().id(),
            key
        );

        // Création de l'entité UploadedPhoto et liaison avec l'artisan
        UploadedPhoto photo = new UploadedPhoto();
        photo.setUploadedPhotoUrl(publicUrl);
        photo.setExtension(file.getContentType());
        photo.setArtisan(artisan);
        artisan.getPhotoGallery().add(photo);

        // Sauvegarde de l'artisan (cascade persiste la photo) et récupération de la photo sauvegardée
        artisan = artisanRepo.save(artisan);
        UploadedPhoto savedPhoto = artisan.getPhotoGallery()
            .stream()
            .filter(p -> p.getUploadedPhotoUrl().equals(publicUrl))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Photo non sauvegardée correctement"));

        return savedPhoto;
    }

    // ================= Suppression photo =================

    /**
     * Supprime une photo du portfolio d'un artisan.
     * 
     * Vérifie que l'utilisateur a le droit (propriétaire ou admin), supprime la relation
     * avec l'artisan et sauvegarde les changements.
     *
     * @param artisanId ID de l'artisan
     * @param photoId ID de la photo à supprimer
     * @param currentUser utilisateur courant
     */
    public void removePhoto(UUID artisanId, UUID photoId, User currentUser) {
        // Vérification des droits : l'utilisateur doit être le propriétaire ou un admin
        securityService.checkUserOwnershipOrAdmin(currentUser, artisanId);

        // Récupération de l'artisan
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new EntityNotFoundException("Professionnel non trouvé."));

        // Recherche de la photo à supprimer
        UploadedPhoto photoToRemove = artisan.getPhotoGallery().stream()
            .filter(photo -> photo.getId().equals(photoId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Photo non trouvée."));

        // Suppression de la photo de la collection et rupture de la relation
        artisan.getPhotoGallery().remove(photoToRemove);
        photoToRemove.setArtisan(null); // casse la relation

        // Sauvegarde de l'artisan pour appliquer la suppression (orphanRemoval gère la suppression de la photo)
        artisanRepo.save(artisan);
    }

    // ================= Liste des photos =================

    /**
     * Récupère l'ensemble des photos du portfolio d'un artisan.
     *
     * @param artisanId ID de l'artisan
     * @return liste des UploadedPhoto associées à l'artisan
     */
    public List<UploadedPhoto> getPortfolio(UUID artisanId) {
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new EntityNotFoundException("Artisan non trouvé."));
        return artisan.getPhotoGallery();
    }
}
