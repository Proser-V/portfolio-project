package com.atelierlocal.service;

import org.springframework.stereotype.Service;

import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.AvatarRepo;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Avatar;
import com.atelierlocal.model.UploadedPhoto;
import com.atelierlocal.model.UserRole;
import com.atelierlocal.dto.UpdateArtisanRequest;
import com.atelierlocal.model.Address;

import java.util.UUID;

@Service
public class ArtisanService {

    private final PasswordService passwordService;
    private final ArtisanRepo artisanRepo;
    private final AvatarService avatarService;
    private final AvatarRepo avatarRepo;
    
    // Constructeur
    public ArtisanService(
                PasswordService passwordService,
                ArtisanRepo artisanRepo,
                AvatarService avatarService,
                AvatarRepo avatarRepo
                ) {
        this.passwordService = passwordService;
        this.artisanRepo = artisanRepo;
        this.avatarService = avatarService;
        this.avatarRepo = avatarRepo;
    }

    public Artisan createArtisan(
                    String name,
                    String email,
                    String rawPassword,
                    String bio,
                    String phoneNumber,
                    String siret,
                    Address address,
                    Avatar avatar,
                    ArtisanCategory category
                    ) {
        if (artisanRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        Artisan artisan = new Artisan();
        artisan.setName(name);
        artisan.setEmail(email);
        artisan.setBio(bio);
        artisan.setPhoneNumber(phoneNumber);
        artisan.setAddress(address);
        artisan.setSiret(siret);
        artisan.setAvatar(avatar);
        artisan.setCategory(category);
        artisan.setUserRole(UserRole.ARTISAN);

        String hashed = passwordService.hashPassword(rawPassword);
        artisan.setHashedPassword(hashed);

        return artisanRepo.save(artisan);
    }

    public Artisan addGalleryPhoto(UUID artisanId, UploadedPhoto photo) {
        // Cherche l'artisan
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new RuntimeException("Professionnel non trouvé."));

        photo.setArtisan(artisan); // Etabli le lien entre la photo et l'artisan
        artisan.getPhotoGallery().add(photo); // Ajoute la photo

        return artisanRepo.save(artisan); // Retourne l'artisan mis à jour
    }

    public Artisan removeGalleryPhoto(UUID artisanId, UUID photoId) {
        // Cherche l'artisan
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new RuntimeException("Professionnel non trouvé."));

        // Cherche la photo à supprimer
        UploadedPhoto photoToRemove = artisan.getPhotoGallery().stream() // Créer un objet Stream pour éviter boucle manuelle
            .filter(photo -> photo.getId().equals(photoId)) // Selectionne la photo dont l'ID correspond
            .findFirst() // Assure de prendre la première photo trouvé (sécurité meme si UUID unique), retourne un Optional
            .orElseThrow(() -> new RuntimeException("Photo non trouvé.")); // Si Optional vide, lance une exception

        artisan.getPhotoGallery().remove(photoToRemove); // Supprime la photo
        photoToRemove.setArtisan(null); // casse la relation entre la photo et l'artisan

        // Photo supprimée de la DB grâce à orpheanRemoval = true
        return artisanRepo.save(artisan); // Retourne l'artisan mis à jour
    }

    public void deleteArtisan(UUID atisanId) {
        Artisan artisan = artisanRepo.findById(atisanId)
            .orElseThrow(() -> new RuntimeException("Professionnel non trouvé."));

        artisanRepo.delete(artisan);
    }

    public Artisan updateArtisan(UUID artisanId, UpdateArtisanRequest request) {
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new RuntimeException("Professionnel non trouvé."));

        if (request.getName() != null) { artisan.setName(request.getName()); }
        if (request.getBio() != null) { artisan.setBio(request.getBio()); }
        if (request.getAddress() != null) { artisan.setAddress(request.getAddress()); }
        if (request.getCategory() != null) { artisan.setCategory(request.getCategory()); }
        if (request.getEmail() != null) { artisan.setEmail(request.getEmail()); }
        if (request.getRawPassword() != null) {
            String hashed = passwordService.hashPassword(request.getRawPassword());
            artisan.setHashedPassword(hashed);
        }
        if (request.getPhoneNumber() != null) { artisan.setPhoneNumber(request.getPhoneNumber()); }
        if (request.getSiret() != null) { artisan.setSiret(request.getSiret()); }
        if (request.getAvatar() != null) {
            // Upload de l'image sur AWS S3
            String avatarUrl = avatarService.uploadAvatar(request.getAvatar(), artisanId);

            // Modification de l'entité Avatar
            Avatar avatar = artisan.getAvatar();
            if (avatar == null) {
                avatar = new Avatar();
                avatar.setUser(artisan);
            }
            avatar.setUrl(avatarUrl);
            avatar.setExtension(avatarService.getFileExtension(request.getAvatar()));

            avatarRepo.save(avatar);
        }

        return artisanRepo.save(artisan);
    }
}


