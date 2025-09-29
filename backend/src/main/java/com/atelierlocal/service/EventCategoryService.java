package com.atelierlocal.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.atelierlocal.dto.ArtisanCategoryResponseDTO;
import com.atelierlocal.dto.EventCategoryRequestDTO;
import com.atelierlocal.dto.EventCategoryResponseDTO;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.EventCategory;
import com.atelierlocal.repository.ArtisanCategoryRepo;
import com.atelierlocal.repository.EventCategoryRepo;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class EventCategoryService {
    private final EventCategoryRepo eventCategoryRepo;
    private final ArtisanCategoryRepo artisanCategoryRepo;

    public EventCategoryService(EventCategoryRepo eventCategoryRepo, ArtisanCategoryRepo artisanCategoryRepo) {
        this.eventCategoryRepo = eventCategoryRepo;
        this.artisanCategoryRepo = artisanCategoryRepo;
    }

    public EventCategoryResponseDTO createEventCategory(EventCategoryRequestDTO request) {
        EventCategory category = new EventCategory();
        category.setName(request.getName());
        category.setArtisanCategoryList(fetchArtisanCategories(request.getArtisanCategoryIds()));

        EventCategory saved = eventCategoryRepo.save(category);
        return toResponseDTO(saved);
    }

    public EventCategoryResponseDTO updateEventCategory(UUID id, EventCategoryRequestDTO request) {
        EventCategory category = eventCategoryRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée"));

        category.setName(request.getName());
        category.setArtisanCategoryList(fetchArtisanCategories(request.getArtisanCategoryIds()));

        EventCategory updated = eventCategoryRepo.save(category);
        return toResponseDTO(updated);
    }

    public void deleteEventCategory(UUID id) {
        if (!eventCategoryRepo.existsById(id)) {
            throw new IllegalArgumentException("Catégorie non trouvée " + id);
        }
        eventCategoryRepo.deleteById(id);
    }

    public EventCategoryResponseDTO getEventCategoryById(UUID id) {
        EventCategory category = eventCategoryRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée: " + id));
        return toResponseDTO(category);
    }

    public List<EventCategoryResponseDTO> getAllEventCategories() {
        return eventCategoryRepo.findAll().stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    public List<ArtisanCategoryResponseDTO> getArtisanCategoriesByEvent(UUID eventCategoryId) {
        EventCategory category = eventCategoryRepo.findById(eventCategoryId)
            .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée : " + eventCategoryId));

        return category.getArtisanCategoryList().stream()
            .map(ac -> new ArtisanCategoryResponseDTO(ac.getId(), ac.getName(), ac.getDescription()))
            .collect(Collectors.toList());
    }

    // Utilitaires
    private List<ArtisanCategory> fetchArtisanCategories(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return artisanCategoryRepo.findAllById(ids);
    }

    private EventCategoryResponseDTO toResponseDTO(EventCategory category) {
        List<UUID> artisanIds = category.getArtisanCategoryList().stream()
            .map(ArtisanCategory::getId)
            .collect(Collectors.toList());

            return new EventCategoryResponseDTO(
                category.getId(),
                category.getName(),
                artisanIds,
                category.getCreatedAt(),
                category.getUpdatedAt()
            );
    }
}
