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
    public UploadedPhoto addPhoto(UUID artisanId, MultipartFile file, Artisan currentArtisan) {
        securityService.checkArtisanOnly(currentArtisan);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier fourni.");
        }

        // Vérification type / taille
        List<String> allowedTypes = List.of("image/png", "image/jpeg");
        if (!allowedTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("Type de fichier non autorisé. Seuls PNG et JPEG sont acceptés.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Fichier trop volumineux. Maximum 5 Mo.");
        }

        // Cherche l'artisan
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new EntityNotFoundException("Professionnel non trouvé."));

        // Upload S3
        String key = "portfolio/" + artisanId + "/" + file.getOriginalFilename();
        PutObjectRequest putReq = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .acl("public-read")
            .contentType(file.getContentType())
            .build();

        try {
            s3Client.putObject(putReq, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new RuntimeException("Erreur upload S3", e);
        }

        String publicUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
            bucketName,
            s3Client.serviceClientConfiguration().region().id(),
            key
        );

        // Création de la photo et liaison avec artisan
        UploadedPhoto photo = new UploadedPhoto();
        photo.setUploadedPhotoUrl(publicUrl);
        photo.setExtension(file.getContentType());
        photo.setArtisan(artisan);
        artisan.getPhotoGallery().add(photo);

        // Sauvegarde artisan (cascade = sauvegarde photo) et retour de la photo
        artisan = artisanRepo.save(artisan);
        UploadedPhoto savedPhoto = artisan.getPhotoGallery()
            .stream()
            .filter(p -> p.getUploadedPhotoUrl().equals(publicUrl))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Photo non sauvegardée correctement"));

        return savedPhoto;
    }

    // ================= Suppression photo =================
    public void removePhoto(UUID artisanId, UUID photoId, User currentUser) {
        securityService.checkUserOwnershipOrAdmin(currentUser, artisanId);

        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new EntityNotFoundException("Professionnel non trouvé."));

        UploadedPhoto photoToRemove = artisan.getPhotoGallery().stream()
            .filter(photo -> photo.getId().equals(photoId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Photo non trouvée."));

        artisan.getPhotoGallery().remove(photoToRemove);
        photoToRemove.setArtisan(null); // casse la relation

        artisanRepo.save(artisan); // orphanRemoval supprimera la photo
    }

    // ================= Liste des photos =================
    public List<UploadedPhoto> getPortfolio(UUID artisanId) {
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new EntityNotFoundException("Artisan non trouvé."));
        return artisan.getPhotoGallery();
    }
}
