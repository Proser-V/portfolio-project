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

@Service
@Transactional
public class RecommendationService {

    private final RecommendationRepo recommendationRepo;
    private final ClientRepo clientRepo;
    private final ArtisanRepo artisanRepo;
    private final SecurityService securityService;

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

    public void deleteRecommendation(UUID recommendationId, Client currentClient) {
        securityService.checkAdminOnly(currentClient);
        if (!recommendationRepo.existsById(recommendationId)) {
            throw new IllegalArgumentException("Recommendation non trouvée : " + recommendationId);
        }
        recommendationRepo.deleteById(recommendationId);
    }

    public RecommendationResponseDTO getRecommendation(UUID recommendationId) {
        Recommendation recommendation = recommendationRepo.findById(recommendationId)
            .orElseThrow(() -> new IllegalArgumentException("Recommendation not found: " + recommendationId));
        return toResponseDTO(recommendation);
    }

    public List<RecommendationResponseDTO> getAllRecommendations(Client currentClient) {
        securityService.checkAdminOnly(currentClient);
        return recommendationRepo.findAll().stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    // Utilitaires
    private RecommendationResponseDTO toResponseDTO(Recommendation recommendation) {
        return new RecommendationResponseDTO(
            recommendation.getId(),
            recommendation.getClient().getId(),
            recommendation.getArtisan().getId()
        );
    }
}
