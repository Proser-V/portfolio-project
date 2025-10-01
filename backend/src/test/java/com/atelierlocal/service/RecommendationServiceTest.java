package com.atelierlocal.service;

import com.atelierlocal.dto.RecommendationRequestDTO;
import com.atelierlocal.dto.RecommendationResponseDTO;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.Recommendation;
import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.ClientRepo;
import com.atelierlocal.repository.RecommendationRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecommendationServiceTest {

    @Mock
    private RecommendationRepo recommendationRepo;

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private ArtisanRepo artisanRepo;

    @InjectMocks
    private RecommendationService recommendationService;

    private UUID clientId;
    private UUID artisanId;
    private UUID recommendationId;

    private Client client;
    private Artisan artisan;
    private Recommendation recommendation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        clientId = UUID.randomUUID();
        artisanId = UUID.randomUUID();
        recommendationId = UUID.randomUUID();

        client = new Client();
        client.setId(clientId);

        artisan = new Artisan();
        artisan.setId(artisanId);

        recommendation = new Recommendation();
        recommendation.setId(recommendationId);
        recommendation.setClient(client);
        recommendation.setArtisan(artisan);
    }

    @Test
    void testCreateRecommendation_success() {
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setClientId(clientId);

        when(clientRepo.findById(clientId)).thenReturn(Optional.of(client));
        when(artisanRepo.findById(artisanId)).thenReturn(Optional.of(artisan));
        when(recommendationRepo.save(any(Recommendation.class))).thenReturn(recommendation);

        RecommendationResponseDTO response = recommendationService.createRecommendation(artisanId, request);

        assertNotNull(response);
        assertEquals(recommendationId, response.getId());
        assertEquals(clientId, response.getClientId());
        assertEquals(artisanId, response.getArtisanId());
    }

    @Test
    void testCreateRecommendation_clientNotFound() {
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setClientId(clientId);

        when(clientRepo.findById(clientId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            recommendationService.createRecommendation(artisanId, request)
        );

        assertTrue(ex.getMessage().contains("Client non trouvé"));
    }

    @Test
    void testCreateRecommendation_artisanNotFound() {
        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setClientId(clientId);

        when(clientRepo.findById(clientId)).thenReturn(Optional.of(client));
        when(artisanRepo.findById(artisanId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            recommendationService.createRecommendation(artisanId, request)
        );

        assertTrue(ex.getMessage().contains("Artisan non trouvé"));
    }

    @Test
    void testDeleteRecommendation_success() {
        when(recommendationRepo.existsById(recommendationId)).thenReturn(true);
        doNothing().when(recommendationRepo).deleteById(recommendationId);

        recommendationService.deleteRecommendation(recommendationId);

        verify(recommendationRepo, times(1)).deleteById(recommendationId);
    }

    @Test
    void testDeleteRecommendation_notFound() {
        when(recommendationRepo.existsById(recommendationId)).thenReturn(false);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            recommendationService.deleteRecommendation(recommendationId)
        );

        assertTrue(ex.getMessage().contains("Recommendation non trouvée"));
    }

    @Test
    void testGetRecommendation_success() {
        when(recommendationRepo.findById(recommendationId)).thenReturn(Optional.of(recommendation));

        RecommendationResponseDTO response = recommendationService.getRecommendation(recommendationId);

        assertNotNull(response);
        assertEquals(recommendationId, response.getId());
        assertEquals(clientId, response.getClientId());
        assertEquals(artisanId, response.getArtisanId());
    }

    @Test
    void testGetRecommendation_notFound() {
        when(recommendationRepo.findById(recommendationId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            recommendationService.getRecommendation(recommendationId)
        );

        assertTrue(ex.getMessage().contains("Recommendation not found"));
    }

    @Test
    void testGetAllRecommendations_success() {
        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(UUID.randomUUID());
        recommendation2.setClient(client);
        recommendation2.setArtisan(artisan);

        when(recommendationRepo.findAll()).thenReturn(Arrays.asList(recommendation, recommendation2));

        List<RecommendationResponseDTO> responses = recommendationService.getAllRecommendations();

        assertEquals(2, responses.size());
        assertEquals(recommendationId, responses.get(0).getId());
    }
}
