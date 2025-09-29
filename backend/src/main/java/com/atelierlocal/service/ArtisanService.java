package com.atelierlocal.service;

import org.springframework.stereotype.Service;

import com.atelierlocal.repository.ArtisanCategoryRepo;
import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.AvatarRepo;

import jakarta.persistence.EntityNotFoundException;

import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Avatar;
import com.atelierlocal.model.UploadedPhoto;
import com.atelierlocal.model.UserRole;
import com.atelierlocal.dto.ArtisanRequestDTO;
import com.atelierlocal.dto.ArtisanResponseDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ArtisanService {

    private final PasswordService passwordService;
    private final ArtisanRepo artisanRepo;
    private final AvatarService avatarService;
    private final AvatarRepo avatarRepo;
    private final ArtisanCategoryRepo artisanCategoryRepo;
    
    // Constructeur
    public ArtisanService(
                PasswordService passwordService,
                ArtisanRepo artisanRepo,
                AvatarService avatarService,
                AvatarRepo avatarRepo,
                ArtisanCategoryRepo artisanCategoryRepo
                ) {
        this.passwordService = passwordService;
        this.artisanRepo = artisanRepo;
        this.avatarService = avatarService;
        this.avatarRepo = avatarRepo;
        this.artisanCategoryRepo = artisanCategoryRepo;
    }

    public ArtisanResponseDTO createArtisan(ArtisanRequestDTO dto) {
        if (artisanRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email déjà utilisé..");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Le mot de passe ne peut être vide.");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Le nom ne peut être vide.");
        }
        if (dto.getCategoryName() == null) {
            throw new IllegalArgumentException("La catégorie d'artisan ne peut être vide.");
        }
        if (dto.getLatitude() == null) {
            throw new IllegalArgumentException("La latitude ne peut être vide.");
        }
        if (dto.getLongitude() == null) {
            throw new IllegalArgumentException("La longitude ne peut être vide.");
        }
        Avatar avatar = null;
        if (dto.getAvatar() != null) {
            String avatarUrl = avatarService.uploadAvatar(dto.getAvatar(), null);
            avatar = new Avatar();
            avatar.setAvatarUrl(avatarUrl);
            avatar.setExtension(avatarService.getFileExtension(dto.getAvatar()));
        }
        ArtisanCategory category = artisanCategoryRepo.findByNameIgnoreCase(dto.getCategoryName())
            .orElseThrow(() -> new IllegalArgumentException("Catégorie invalide"));

        Artisan artisan = new Artisan();
        artisan.setName(dto.getName());
        artisan.setEmail(dto.getEmail());
        artisan.setBio(dto.getBio());
        artisan.setPhoneNumber(dto.getPhoneNumber());
        artisan.setLatitude(dto.getLatitude());
        artisan.setLongitude(dto.getLongitude());
        artisan.setSiret(dto.getSiret());
        artisan.setAvatar(avatar);
        artisan.setCategory(category);
        artisan.setUserRole(UserRole.ARTISAN);
        artisan.setActive(true);

        String hashed = passwordService.hashPassword(dto.getPassword());
        artisan.setHashedPassword(hashed);

        Artisan savedArtisan = artisanRepo.save(artisan);
        return new ArtisanResponseDTO(savedArtisan);
    }

    public Artisan addGalleryPhoto(UUID artisanId, UploadedPhoto photo) {
        // Cherche l'artisan
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new EntityNotFoundException("Professionnel non trouvé."));

        photo.setArtisan(artisan); // Etabli le lien entre la photo et l'artisan
        artisan.getPhotoGallery().add(photo); // Ajoute la photo

        return artisanRepo.save(artisan); // Retourne l'artisan mis à jour
    }

    public Artisan removeGalleryPhoto(UUID artisanId, UUID photoId) {
        // Cherche l'artisan
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new EntityNotFoundException("Professionnel non trouvé."));

        // Cherche la photo à supprimer
        UploadedPhoto photoToRemove = artisan.getPhotoGallery().stream() // Créer un objet Stream pour éviter boucle manuelle
            .filter(photo -> photo.getId().equals(photoId)) // Selectionne la photo dont l'ID correspond
            .findFirst() // Assure de prendre la première photo trouvé (sécurité meme si UUID unique), retourne un Optional
            .orElseThrow(() -> new EntityNotFoundException("Photo non trouvé.")); // Si Optional vide, lance une exception

        artisan.getPhotoGallery().remove(photoToRemove); // Supprime la photo
        photoToRemove.setArtisan(null); // casse la relation entre la photo et l'artisan

        // Photo supprimée de la DB grâce à orpheanRemoval = true
        return artisanRepo.save(artisan); // Retourne l'artisan mis à jour
    }

    public void deleteArtisan(UUID atisanId) {
        Artisan artisan = artisanRepo.findById(atisanId)
            .orElseThrow(() -> new EntityNotFoundException("Professionnel non trouvé."));

        artisanRepo.delete(artisan);
    }

    public ArtisanResponseDTO updateArtisan(UUID artisanId, ArtisanRequestDTO request) {
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new EntityNotFoundException("Professionnel non trouvé."));

        if (request.getLatitude() != null) { artisan.setLatitude(request.getLatitude()); }
        if (request.getLongitude() != null) { artisan.setLongitude(request.getLongitude()); }
        if (request.getCategoryName() != null) {
            ArtisanCategory category = artisanCategoryRepo.findByNameIgnoreCase(request.getCategoryName())
                .orElseThrow(() -> new IllegalArgumentException("Catégorie invalide"));
            artisan.setCategory(category);
        }
        if (request.getName() != null) { artisan.setName(request.getName()); }
        if (request.getBio() != null) { artisan.setBio(request.getBio()); }
        if (request.getEmail() != null) { artisan.setEmail(request.getEmail()); }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            String hashed = passwordService.hashPassword(request.getPassword());
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
            avatar.setAvatarUrl(avatarUrl);
            avatar.setExtension(avatarService.getFileExtension(request.getAvatar()));

            avatarRepo.save(avatar);
        }
        Artisan updatedArtisan = artisanRepo.save(artisan);
        return new ArtisanResponseDTO(updatedArtisan);
    }

    public ArtisanResponseDTO getArtisanById(UUID artisanId) {
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new EntityNotFoundException("Professionnel non trouvé."));
        return new ArtisanResponseDTO(artisan);
    }

    public ArtisanResponseDTO getArtisanByEmail(String email) {
        Artisan artisan = artisanRepo.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Professionnel non trouvé."));
        return new ArtisanResponseDTO(artisan);
    }

    public List<ArtisanResponseDTO> getAllArtisans() {
        return artisanRepo.findAll().stream()
                                .map(ArtisanResponseDTO::new)
                                .collect(Collectors.toList());
    }

    public List<ArtisanResponseDTO> getAllArtisansByCategory(UUID categoryId) {
        ArtisanCategory category = artisanCategoryRepo.findById(categoryId)
            .orElseThrow(() -> new EntityNotFoundException("Categorie non trouvée."));

        return artisanRepo.findAllByCategory(category).stream()
                                                    .map(ArtisanResponseDTO::new)
                                                    .collect(Collectors.toList());
    }

    public void banArtisan(UUID artisanId) {
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));
        artisan.setActive(false);
        artisanRepo.save(artisan);
    }
}
