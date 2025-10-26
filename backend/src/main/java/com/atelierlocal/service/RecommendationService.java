package com.atelierlocal.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atelierlocal.dto.RecommendationRequestDTO;
import com.atelierlocal.dto.RecommendationResponseDTO;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.Recommendation;
import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.ClientRepo;
import com.atelierlocal.repository.RecommendationRepo;
import com.atelierlocal.security.SecurityService;

/**
 * Service responsable de la gestion des recommandations entre clients et artisans.
 * 
 * Il permet de créer, consulter et supprimer des recommandations.  
 * La couche de sécurité est assurée par {@link SecurityService} afin de restreindre
 * les actions selon le rôle de l'utilisateur (client ou administrateur).
 * 
 * Toutes les opérations sont transactionnelles afin d'assurer la cohérence des données.
 */
@Service
@Transactional
public class RecommendationService {

    /** Dépôt pour la gestion des entités Recommendation. */
    private final RecommendationRepo recommendationRepo;

    /** Dépôt pour la gestion des entités Client. */
    private final ClientRepo clientRepo;

    /** Dépôt pour la gestion des entités Artisan. */
    private final ArtisanRepo artisanRepo;

    /** Service de sécurité vérifiant les autorisations des utilisateurs. */
    private final SecurityService securityService;

    /**
     * Constructeur injectant les dépendances nécessaires.
     */
    public RecommendationService(
        RecommendationRepo recommendationRepo,
        ClientRepo clientRepo,
        ArtisanRepo artisanRepo,
        SecurityService securityService
    ) {
        this.recommendationRepo = recommendationRepo;
        this.clientRepo = clientRepo;
        this.artisanRepo = artisanRepo;
        this.securityService = securityService;
    }

    // ==============================================================
    // Création d’une recommandation
    // ==============================================================

    /**
     * Crée une nouvelle recommandation entre un client et un artisan.
     * 
     * @param artisanId      Identifiant de l'artisan recommandé.
     * @param request        Données de la recommandation (DTO d'entrée).
     * @param currentClient  Client actuellement connecté.
     * @return               La recommandation créée sous forme de DTO de réponse.
     * @throws IllegalArgumentException si le client ou l’artisan n’existe pas.
     */
    public RecommendationResponseDTO createRecommendation(UUID artisanId, RecommendationRequestDTO request, Client currentClient) {
        securityService.checkClientOnly(currentClient);

        Client client = clientRepo.findById(request.getClientId())
            .orElseThrow(() -> new IllegalArgumentException("Client non trouvé : " + request.getClientId()));

        Artisan artisan = artisanRepo.findById(artisanId)
            .orElseThrow(() -> new IllegalArgumentException("Artisan non trouvé : " + artisanId));

        Recommendation recommendation = new Recommendation();
        recommendation.setClient(client);
        recommendation.setArtisan(artisan);

        Recommendation saved = recommendationRepo.save(recommendation);

        return toResponseDTO(saved);
    }

    // ==============================================================
    // Suppression d’une recommandation
    // ==============================================================

    /**
     * Supprime une recommandation à partir de son identifiant.
     * Seuls les administrateurs sont autorisés à effectuer cette action.
     * 
     * @param recommendationId Identifiant de la recommandation à supprimer.
     * @param currentClient    Utilisateur connecté (doit être administrateur).
     * @throws IllegalArgumentException si la recommandation n'existe pas.
     */
    public void deleteRecommendation(UUID recommendationId, Client currentClient) {
        securityService.checkAdminOnly(currentClient);

        if (!recommendationRepo.existsById(recommendationId)) {
            throw new IllegalArgumentException("Recommendation non trouvée : " + recommendationId);
        }

        recommendationRepo.deleteById(recommendationId);
    }

    // ==============================================================
    // Consultation d’une recommandation
    // ==============================================================

    /**
     * Récupère une recommandation spécifique par son identifiant.
     * 
     * @param recommendationId Identifiant de la recommandation recherchée.
     * @return                 DTO représentant la recommandation.
     * @throws IllegalArgumentException si la recommandation n’existe pas.
     */
    public RecommendationResponseDTO getRecommendation(UUID recommendationId) {
        Recommendation recommendation = recommendationRepo.findById(recommendationId)
            .orElseThrow(() -> new IllegalArgumentException("Recommendation not found: " + recommendationId));

        return toResponseDTO(recommendation);
    }

    // ==============================================================
    // Consultation de toutes les recommandations
    // ==============================================================

    /**
     * Récupère la liste complète des recommandations enregistrées.
     * 
     * Accessible uniquement par un administrateur.
     * 
     * @param currentClient Client connecté (doit être administrateur).
     * @return              Liste des recommandations sous forme de DTO.
     */
    public List<RecommendationResponseDTO> getAllRecommendations(Client currentClient) {
        securityService.checkAdminOnly(currentClient);

        return recommendationRepo.findAll().stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    // ==============================================================
    // Méthode utilitaire
    // ==============================================================

    /**
     * Convertit une entité {@link Recommendation} en DTO {@link RecommendationResponseDTO}.
     * 
     * @param recommendation Entité Recommendation à convertir.
     * @return DTO représentant la recommandation.
     */
    private RecommendationResponseDTO toResponseDTO(Recommendation recommendation) {
        return new RecommendationResponseDTO(
            recommendation.getId(),
            recommendation.getClient().getId(),
            recommendation.getArtisan().getId()
        );
    }
}
