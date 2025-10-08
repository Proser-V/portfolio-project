package com.atelierlocal.service;

import org.springframework.stereotype.Service;

import com.atelierlocal.repository.ArtisanCategoryRepo;
import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.AvatarRepo;
import com.atelierlocal.security.SecurityService;

import jakarta.persistence.EntityNotFoundException;

import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Avatar;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.User;
import com.atelierlocal.model.UserRole;
import com.atelierlocal.dto.ArtisanRequestDTO;
import com.atelierlocal.dto.ArtisanResponseDTO;

import java.util.Collections;
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
    private final SecurityService securityService;
    
    // Constructeur
    public ArtisanService(
                PasswordService passwordService,
                ArtisanRepo artisanRepo,
                AvatarService avatarService,
                AvatarRepo avatarRepo,
                ArtisanCategoryRepo artisanCategoryRepo,
                SecurityService securityService
                ) {
        this.passwordService = passwordService;
        this.artisanRepo = artisanRepo;
        this.avatarService = avatarService;
        this.avatarRepo = avatarRepo;
        this.artisanCategoryRepo = artisanCategoryRepo;
        this.securityService = securityService;
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

    public void deleteArtisan(UUID atisanId, Client currentClient) {
        securityService.checkAdminOnly(currentClient);
        Artisan artisan = artisanRepo.findById(atisanId)
            .orElseThrow(() -> new EntityNotFoundException("Professionnel non trouvé."));

        artisanRepo.delete(artisan);
    }

    public ArtisanResponseDTO updateArtisan(UUID artisanId, ArtisanRequestDTO request, User currentUser) {
        securityService.checkUserOwnershipOrAdmin(currentUser, artisanId);
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

    public ArtisanResponseDTO getArtisanByEmail(String email, Client currentClient) {
        securityService.checkAdminOnly(currentClient);
        Artisan artisan = artisanRepo.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Professionnel non trouvé."));
        return new ArtisanResponseDTO(artisan);
    }

    public List<ArtisanResponseDTO> getAllArtisans(Client currentClient) {
        securityService.checkClientOrAdmin(currentClient);
        return artisanRepo.findAll().stream()
                                .map(ArtisanResponseDTO::new)
                                .collect(Collectors.toList());
    }

    public List<ArtisanResponseDTO> getAllArtisansByCategory(UUID categoryId, Client currentClient) {
        securityService.checkClientOrAdmin(currentClient);
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

    public List<Artisan> getRandomTopArtisans(int count) {
        List<Artisan> top10 = artisanRepo.findTop10ByOrderByRecommendationsDesc();
        if (top10.isEmpty()) return Collections.emptyList();
        Collections.shuffle(top10);
        return top10.subList(0, Math.min(count, top10.size()));
    }
}
