package com.atelierlocal.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.atelierlocal.dto.ArtisanRequestDTO;
import com.atelierlocal.dto.ArtisanResponseDTO;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Avatar;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.User;
import com.atelierlocal.model.UserRole;
import com.atelierlocal.repository.ArtisanCategoryRepo;
import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.AttachmentRepo;
import com.atelierlocal.repository.AvatarRepo;
import com.atelierlocal.repository.MessageRepo;
import com.atelierlocal.security.SecurityService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

/**
 * Service métier pour gérer les artisans.
 * 
 * Fournit des méthodes pour :
 * - créer, mettre à jour et supprimer des artisans,
 * - gérer l'avatar et la catégorie d'un artisan,
 * - récupérer des artisans par ID, email ou catégorie,
 * - appliquer des contrôles d'accès via SecurityService,
 * - gérer le bannissement et récupérer des artisans aléatoires.
 */
@Service
public class ArtisanService {

    private final PasswordService passwordService;
    private final ArtisanRepo artisanRepo;
    private final MessageRepo messageRepo;
    private final AttachmentRepo attachmentRepo;
    private final AvatarService avatarService;
    private final AvatarRepo avatarRepo;
    private final ArtisanCategoryRepo artisanCategoryRepo;
    private final SecurityService securityService;
    
    /**
     * Constructeur avec injection des dépendances nécessaires.
     */
    public ArtisanService(
                PasswordService passwordService,
                ArtisanRepo artisanRepo,
                MessageRepo messageRepo,
                AttachmentRepo attachmentRepo,
                AvatarService avatarService,
                AvatarRepo avatarRepo,
                ArtisanCategoryRepo artisanCategoryRepo,
                SecurityService securityService
                ) {
        this.passwordService = passwordService;
        this.artisanRepo = artisanRepo;
        this.messageRepo = messageRepo;
        this.attachmentRepo = attachmentRepo;
        this.avatarService = avatarService;
        this.avatarRepo = avatarRepo;
        this.artisanCategoryRepo = artisanCategoryRepo;
        this.securityService = securityService;
    }

    /**
     * Crée un nouvel artisan à partir d'un DTO de requête.
     * 
     * @param dto données de l'artisan
     * @return DTO de réponse avec l'artisan créé
     * @throws IllegalArgumentException si des champs obligatoires sont manquants ou invalides
     */
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

        // Gestion de l'avatar
        Avatar avatar = null;
        if (dto.getAvatar() != null) {
            String avatarUrl = avatarService.uploadAvatar(dto.getAvatar(), null);
            avatar = new Avatar();
            avatar.setAvatarUrl(avatarUrl);
            avatar.setExtension(avatarService.getFileExtension(dto.getAvatar()));
            avatar.setUser(artisan);
        }
        artisan.setAvatar(avatar);
        artisan.setCategory(category);
        artisan.setUserRole(UserRole.ARTISAN);
        artisan.setActive(true);

        String hashed = passwordService.hashPassword(dto.getPassword());
        artisan.setHashedPassword(hashed);

        Artisan savedArtisan = artisanRepo.save(artisan);
        return new ArtisanResponseDTO(savedArtisan);
    }

    @Transactional
    /**
     * Supprime un artisan donné par son ID.
     * 
     * @param atisanId ID de l'artisan
     * @param currentClient utilisateur courant pour vérification des droits admin
     * @throws EntityNotFoundException si l'artisan n'existe pas
     */
    public void deleteArtisan(UUID artisanId, Client currentClient) {
        securityService.checkAdminOnly(currentClient);
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new EntityNotFoundException("Professionnel non trouvé."));

        attachmentRepo.deleteByUserId(artisanId);

        messageRepo.deleteByUserId(artisanId);

        artisanRepo.delete(artisan);
    }

    /**
     * Met à jour un artisan existant.
     * 
     * @param artisanId ID de l'artisan à mettre à jour
     * @param request DTO avec les champs à modifier
     * @param currentUser utilisateur courant pour vérification des droits
     * @return DTO de réponse avec l'artisan mis à jour
     * @throws EntityNotFoundException si l'artisan n'existe pas
     */
    public ArtisanResponseDTO updateArtisan(UUID artisanId, ArtisanRequestDTO request, User currentUser) {
        securityService.checkUserOwnershipOrAdmin(currentUser, artisanId);
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new EntityNotFoundException("Professionnel non trouvé."));

        // Mise à jour des champs si présents
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

        // Gestion de l'avatar
        if (request.getAvatar() != null) {
            String avatarUrl = avatarService.uploadAvatar(request.getAvatar(), artisanId);
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

    /**
     * Récupère un artisan par son ID.
     */
    public ArtisanResponseDTO getArtisanById(UUID artisanId) {
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new EntityNotFoundException("Professionnel non trouvé."));
        return new ArtisanResponseDTO(artisan);
    }

    /**
     * Récupère un artisan par email (accessible seulement aux admins).
     */
    public ArtisanResponseDTO getArtisanByEmail(String email, Client currentClient) {
        securityService.checkAdminOnly(currentClient);
        Artisan artisan = artisanRepo.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Professionnel non trouvé."));
        return new ArtisanResponseDTO(artisan);
    }

    /**
     * Récupère tous les artisans accessibles pour un client ou admin.
     */
    public List<ArtisanResponseDTO> getAllArtisans(Client currentClient) {
        if (currentClient != null) {
            securityService.checkClientOrAdmin(currentClient);
        }
        return artisanRepo.findAll().stream()
                                .map(ArtisanResponseDTO::new)
                                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les artisans d'une catégorie spécifique.
     */
    public List<ArtisanResponseDTO> getAllArtisansByCategory(UUID categoryId, Client currentClient) {
        securityService.checkClientOrAdmin(currentClient);
        ArtisanCategory category = artisanCategoryRepo.findById(categoryId)
            .orElseThrow(() -> new EntityNotFoundException("Categorie non trouvée."));

        return artisanRepo.findAllByCategory(category).stream()
                                                    .map(ArtisanResponseDTO::new)
                                                    .collect(Collectors.toList());
    }

    /**
     * Désactive (bannit) un artisan.
     */
    public void banArtisan(UUID artisanId) {
        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));
        artisan.setActive(false);
        artisanRepo.save(artisan);
    }

    /**
     * Récupère un nombre aléatoire d'artisans parmi les top recommandés.
     */
    public List<Artisan> getRandomTopArtisans(int count) {
        List<Artisan> top10 = artisanRepo.findTop10ByOrderByRecommendationsDesc();
        if (top10.isEmpty()) return Collections.emptyList();
        Collections.shuffle(top10);
        return top10.subList(0, Math.min(count, top10.size()));
    }
}
