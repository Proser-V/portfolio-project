package com.atelierlocal.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atelierlocal.dto.ArtisanCategoryRequestDTO;
import com.atelierlocal.dto.ArtisanCategoryResponseDTO;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.EventCategory;
import com.atelierlocal.repository.ArtisanCategoryRepo;
import com.atelierlocal.repository.EventCategoryRepo;

import jakarta.persistence.EntityNotFoundException;

/**
 * Service métier pour gérer les catégories d'artisans.
 * 
 * Fournit des méthodes pour :
 * - créer, mettre à jour et supprimer des catégories d'artisans,
 * - récupérer une ou plusieurs catégories d'artisans,
 * - filtrer les catégories par événement.
 */
@Service
public class ArtisanCategoryService {

    private final ArtisanCategoryRepo artisanCategoryRepo;

    /**
     * Constructeur avec injection du repository de catégories d'artisans.
     * 
     * @param artisanCategoryRepo repository pour accéder aux données ArtisanCategory
     * @param eventCategoryRepo repository pour accéder aux EventCategory (non utilisé ici)
     */
    public ArtisanCategoryService(ArtisanCategoryRepo artisanCategoryRepo, EventCategoryRepo eventCategoryRepo) {
        this.artisanCategoryRepo = artisanCategoryRepo;
    }

    /**
     * Crée une nouvelle catégorie d'artisan à partir d'un DTO de requête.
     * 
     * @param dto données de la nouvelle catégorie
     * @return DTO de réponse contenant la catégorie créée
     * @throws IllegalArgumentException si le nom ou la description est manquant
     */
    public ArtisanCategoryResponseDTO createArtisanCategory(
                                ArtisanCategoryRequestDTO dto
                                ) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Le nom doit être renseigné.");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new IllegalArgumentException("La description doit être renseignée.");
        }

        ArtisanCategory artisanCategory = new ArtisanCategory();
        artisanCategory.setName(dto.getName());
        artisanCategory.setDescription(dto.getDescription());

        ArtisanCategory savedCategory = artisanCategoryRepo.save(artisanCategory);
        return new ArtisanCategoryResponseDTO(
            savedCategory.getId(),
            savedCategory.getName(),
            savedCategory.getDescription()
        );
    }

    /**
     * Supprime une catégorie d'artisan par son ID.
     * 
     * @param artisanCategoryId ID de la catégorie à supprimer
     * @throws EntityNotFoundException si la catégorie n'existe pas
     */
    public void deleteArtisanCategory(UUID artisanCategoryId) {
        ArtisanCategory artisanCategory = artisanCategoryRepo.findById(artisanCategoryId)
            .orElseThrow(() -> new EntityNotFoundException("Catégorie d'artisan non trouvée."));
        
        artisanCategoryRepo.delete(artisanCategory);
    }

    /**
     * Met à jour une catégorie d'artisan existante.
     * 
     * @param artisanCategoryId ID de la catégorie à mettre à jour
     * @param dto données mises à jour
     * @return DTO de réponse avec la catégorie mise à jour
     * @throws EntityNotFoundException si la catégorie n'existe pas
     */
    public ArtisanCategoryResponseDTO updateArtisanCategory(UUID artisanCategoryId, ArtisanCategoryRequestDTO dto) {
        ArtisanCategory artisanCategory = artisanCategoryRepo.findById(artisanCategoryId)
            .orElseThrow(() -> new EntityNotFoundException("Catégorie d'artisan non trouvée."));

        if (dto.getName() != null) { artisanCategory.setName(dto.getName()); }
        if (dto.getDescription() != null) { artisanCategory.setDescription(dto.getDescription()); }
        ArtisanCategory savedCategory = artisanCategoryRepo.save(artisanCategory);

        return new ArtisanCategoryResponseDTO(
            savedCategory.getId(),
            savedCategory.getName(),
            savedCategory.getDescription()
        );
    }

    /**
     * Récupère toutes les catégories d'artisans.
     * 
     * @return liste de DTO de réponse
     * @throws EntityNotFoundException si aucune catégorie n'est trouvée
     */
    public List<ArtisanCategoryResponseDTO> getAllArtisanCategory() {
        List<ArtisanCategory> artisanCategoriesList = artisanCategoryRepo.findAll();
        if (artisanCategoriesList.isEmpty()) {
            throw new EntityNotFoundException("Aucune Catégorie d'artisan trouvée.");
        }
        return artisanCategoriesList.stream()
            .map(category -> new ArtisanCategoryResponseDTO(
                                    category.getId(),
                                    category.getName(),
                                    category.getDescription()
            ))
            .toList();
    }

    /**
     * Récupère une catégorie d'artisan par son ID.
     * 
     * @param artisanCategorieId ID de la catégorie
     * @return DTO de réponse avec la catégorie trouvée
     * @throws EntityNotFoundException si la catégorie n'existe pas
     */
    public ArtisanCategoryResponseDTO getArtisanCategoryById(UUID artisanCategorieId) {
        ArtisanCategory artisanCategory = artisanCategoryRepo.findById(artisanCategorieId)
            .orElseThrow(() -> new EntityNotFoundException("Catégorie d'artisan non trouvée."));

        return new ArtisanCategoryResponseDTO(
                    artisanCategory.getId(),
                    artisanCategory.getName(),
                    artisanCategory.getDescription()
        );
    }

    /**
     * Récupère les catégories d'artisans associées à un événement.
     * 
     * @param eventCategory événement utilisé pour filtrer les catégories
     * @return liste de DTO de réponse
     */
    public List<ArtisanCategoryResponseDTO> getArtisanCategoriesByEvent(EventCategory eventCategory) {
        return artisanCategoryRepo.findByEventCategories(eventCategory)
            .stream()
            .map(category -> new ArtisanCategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription()
            ))
            .toList();
    }
}
