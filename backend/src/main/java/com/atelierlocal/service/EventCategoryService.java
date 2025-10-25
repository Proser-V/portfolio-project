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

/**
 * Service pour la gestion des catégories d'événements.
 * 
 * Fournit des méthodes pour :
 * - créer, mettre à jour et supprimer des catégories d'événements,
 * - récupérer des catégories par ID ou toutes les catégories,
 * - récupérer les catégories d'artisans associées à un événement.
 */
@Transactional
@Service
public class EventCategoryService {

    private final EventCategoryRepo eventCategoryRepo;
    private final ArtisanCategoryRepo artisanCategoryRepo;

    public EventCategoryService(EventCategoryRepo eventCategoryRepo, ArtisanCategoryRepo artisanCategoryRepo) {
        this.eventCategoryRepo = eventCategoryRepo;
        this.artisanCategoryRepo = artisanCategoryRepo;
    }

    /**
     * Crée une nouvelle catégorie d'événement.
     * 
     * @param request DTO contenant les informations de la catégorie
     * @return DTO de la catégorie créée
     */
    public EventCategoryResponseDTO createEventCategory(EventCategoryRequestDTO request) {
        EventCategory category = new EventCategory();
        category.setName(request.getName());
        // Récupère la liste des catégories d'artisans associées
        category.setArtisanCategoryList(fetchArtisanCategories(request.getArtisanCategoryIds()));

        EventCategory saved = eventCategoryRepo.save(category);
        return toResponseDTO(saved);
    }

    /**
     * Met à jour une catégorie d'événement existante.
     * 
     * @param id ID de la catégorie à mettre à jour
     * @param request DTO contenant les nouvelles informations
     * @return DTO de la catégorie mise à jour
     * @throws IllegalArgumentException si la catégorie n'existe pas
     */
    public EventCategoryResponseDTO updateEventCategory(UUID id, EventCategoryRequestDTO request) {
        EventCategory category = eventCategoryRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée"));

        category.setName(request.getName());
        category.setArtisanCategoryList(fetchArtisanCategories(request.getArtisanCategoryIds()));

        EventCategory updated = eventCategoryRepo.save(category);
        return toResponseDTO(updated);
    }

    /**
     * Supprime une catégorie d'événement.
     * 
     * @param id ID de la catégorie à supprimer
     * @throws IllegalArgumentException si la catégorie n'existe pas
     */
    public void deleteEventCategory(UUID id) {
        if (!eventCategoryRepo.existsById(id)) {
            throw new IllegalArgumentException("Catégorie non trouvée " + id);
        }
        eventCategoryRepo.deleteById(id);
    }

    /**
     * Récupère une catégorie d'événement par son ID.
     * 
     * @param id ID de la catégorie
     * @return DTO de la catégorie
     * @throws IllegalArgumentException si la catégorie n'existe pas
     */
    public EventCategoryResponseDTO getEventCategoryById(UUID id) {
        EventCategory category = eventCategoryRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée: " + id));
        return toResponseDTO(category);
    }

    /**
     * Récupère toutes les catégories d'événements.
     * 
     * @return liste de DTO de toutes les catégories
     */
    public List<EventCategoryResponseDTO> getAllEventCategories() {
        return eventCategoryRepo.findAll().stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * Récupère les catégories d'artisans associées à une catégorie d'événement.
     * 
     * @param eventCategoryId ID de la catégorie d'événement
     * @return liste de DTO des catégories d'artisans associées
     * @throws IllegalArgumentException si la catégorie d'événement n'existe pas
     */
    public List<ArtisanCategoryResponseDTO> getArtisanCategoriesByEvent(UUID eventCategoryId) {
        EventCategory category = eventCategoryRepo.findById(eventCategoryId)
            .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée : " + eventCategoryId));

        return category.getArtisanCategoryList().stream()
            .map(ac -> new ArtisanCategoryResponseDTO(ac.getId(), ac.getName(), ac.getDescription()))
            .collect(Collectors.toList());
    }

    // --- Méthodes utilitaires ---

    /**
     * Récupère les catégories d'artisans à partir d'une liste d'IDs.
     * 
     * @param ids liste d'IDs des catégories d'artisans
     * @return liste des catégories d'artisans correspondantes
     */
    private List<ArtisanCategory> fetchArtisanCategories(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return artisanCategoryRepo.findAllById(ids);
    }

    /**
     * Convertit une entité EventCategory en DTO.
     * 
     * @param category entité à convertir
     * @return DTO correspondant
     */
    private EventCategoryResponseDTO toResponseDTO(EventCategory category) {
            return new EventCategoryResponseDTO(category);
    }
}
