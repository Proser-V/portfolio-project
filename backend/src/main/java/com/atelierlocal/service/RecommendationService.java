package com.atelierlocal.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.Recommendation;
import com.atelierlocal.repository.RecommendationRepo;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RecommendationService {
    private final RecommendationRepo recommendationRepo;

    public RecommendationService(RecommendationRepo recommendationRepo) {
        this.recommendationRepo = recommendationRepo;
    }

    public Recommendation createRecommendation(Client client, Artisan artisan) {
        Recommendation recommendation = new Recommendation();
        recommendation.setClient(client);
        recommendation.setArtisan(artisan);
        return recommendationRepo.save(recommendation);
    }

    public void deleteRecomnnedation(UUID recommendationId) {
        if (!recommendationRepo.existsById(recommendationId)) {
            throw new IllegalArgumentException("Recommendation non trouvée dans la base");
        }
        recommendationRepo.deleteById(recommendationId);
    }

    public Recommendation getRecommendation(UUID recommendationId) {
        return recommendationRepo.findById(recommendationId)
            .orElseThrow(() -> new IllegalArgumentException("Recommendation non trouvée dans la base"));
    }

    public List<Recommendation> getAllRecommendations() {
        return recommendationRepo.findAll();
    }
}
