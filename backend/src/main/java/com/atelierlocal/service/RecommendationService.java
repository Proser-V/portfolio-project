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

@Service
@Transactional
public class RecommendationService {

    private final RecommendationRepo recommendationRepo;
    private final ClientRepo clientRepo;
    private final ArtisanRepo artisanRepo;

    public RecommendationService(
        RecommendationRepo recommendationRepo,
        ClientRepo clientRepo,
        ArtisanRepo artisanRepo
    ) {
        this.recommendationRepo = recommendationRepo;
        this.clientRepo = clientRepo;
        this.artisanRepo = artisanRepo;
    }

    public RecommendationResponseDTO createRecommendation(RecommendationRequestDTO request) {
        Client client = clientRepo.findById(request.getClientId())
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + request.getClientId()));
        Artisan artisan = artisanRepo.findById(request.getArtisanId())
            .orElseThrow(() -> new IllegalArgumentException("Artisan not found: " + request.getArtisanId()));

        Recommendation recommendation = new Recommendation();
        recommendation.setClient(client);
        recommendation.setArtisan(artisan);

        Recommendation saved = recommendationRepo.save(recommendation);

        return toResponseDTO(saved);
    }

    public void deleteRecommendation(UUID recommendationId) {
        if (!recommendationRepo.existsById(recommendationId)) {
            throw new IllegalArgumentException("Recommendation not found: " + recommendationId);
        }
        recommendationRepo.deleteById(recommendationId);
    }

    public RecommendationResponseDTO getRecommendation(UUID recommendationId) {
        Recommendation recommendation = recommendationRepo.findById(recommendationId)
            .orElseThrow(() -> new IllegalArgumentException("Recommendation not found: " + recommendationId));
        return toResponseDTO(recommendation);
    }

    public List<RecommendationResponseDTO> getAllRecommendations() {
        return recommendationRepo.findAll().stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    private RecommendationResponseDTO toResponseDTO(Recommendation recommendation) {
        return new RecommendationResponseDTO(
            recommendation.getId(),
            recommendation.getClient().getId(),
            recommendation.getArtisan().getId(),
            recommendation.getCreatedAt()
        );
    }
}
