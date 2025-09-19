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

@Service
public class ArtisanCategoryService {

    private final ArtisanCategoryRepo artisanCategoryRepo;
    private final EventCategoryRepo eventCategoryRepo;

    public ArtisanCategoryService(ArtisanCategoryRepo artisanCategoryRepo, EventCategoryRepo eventCategoryRepo) {
        this.artisanCategoryRepo = artisanCategoryRepo;
        this.eventCategoryRepo = eventCategoryRepo;
    }

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

        if (dto.getEventCategoryIds() != null) {
            List<EventCategory> events = eventCategoryRepo.findAllById(dto.getEventCategoryIds());
            artisanCategory.setEventCategories(events);
            for (EventCategory eventCategory : events) {
                eventCategory.getArtisanCategoryList().add(artisanCategory);
            }
        }

        ArtisanCategory savedCategory = artisanCategoryRepo.save(artisanCategory);
        return new ArtisanCategoryResponseDTO(
            savedCategory.getId(),
            savedCategory.getName(),
            savedCategory.getDescription()
        );
    }

    public void deleteArtisanCategory(UUID artisanCategoryId) {
        ArtisanCategory artisanCategory = artisanCategoryRepo.findById(artisanCategoryId)
            .orElseThrow(() -> new EntityNotFoundException("Catégorie d'artisan non trouvée."));
        
        artisanCategoryRepo.delete(artisanCategory);
    }

    public ArtisanCategoryResponseDTO updateArtisanCategory(UUID artisanCategoryId, ArtisanCategoryRequestDTO dto) {
        ArtisanCategory artisanCategory = artisanCategoryRepo.findById(artisanCategoryId)
            .orElseThrow(() -> new EntityNotFoundException("Catégorie d'artisan non trouvée."));

        if (dto.getName() != null) { artisanCategory.setName(dto.getName()); }
        if (dto.getDescription() != null) { artisanCategory.setDescription(dto.getDescription()); }
        if (dto.getEventCategoryIds() != null) {
            List<EventCategory> events = eventCategoryRepo.findAllById(dto.getEventCategoryIds());
            artisanCategory.setEventCategories(events);
            for (EventCategory eventCategory : events) {
                eventCategory.getArtisanCategoryList().add(artisanCategory);
            }
        }
        ArtisanCategory savedCategory = artisanCategoryRepo.save(artisanCategory);

        return new ArtisanCategoryResponseDTO(
            savedCategory.getId(),
            savedCategory.getName(),
            savedCategory.getDescription()
        );
    }

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

    public ArtisanCategoryResponseDTO getArtisanCategoryById(UUID artisanCategorieId) {
        ArtisanCategory artisanCategory = artisanCategoryRepo.findById(artisanCategorieId)
            .orElseThrow(() -> new EntityNotFoundException("Catégorie d'artisan non trouvée."));

        return new ArtisanCategoryResponseDTO(
                    artisanCategory.getId(),
                    artisanCategory.getName(),
                    artisanCategory.getDescription()
        );
    }

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
