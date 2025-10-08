package com.atelierlocal.controller;

import com.atelierlocal.model.Client;
import com.atelierlocal.dto.RecommendationResponseDTO;
import com.atelierlocal.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecommendationControllerTest {

    private RecommendationController recommendationController;
    private RecommendationService recommendationService;

    private Client mockClient;
    private UUID recommendationId;
    private RecommendationResponseDTO mockRecommendation;

    @BeforeEach
    void setUp() {
        recommendationService = mock(RecommendationService.class);
        recommendationController = new RecommendationController(recommendationService);

        mockClient = new Client();
        mockClient.setId(UUID.randomUUID());

        recommendationId = UUID.randomUUID();
        mockRecommendation = new RecommendationResponseDTO(
                recommendationId,
                UUID.randomUUID(), // artisanId
                mockClient.getId() // clientId
        );
    }

    @Test
    void testGetAllRecommendations() {
        when(recommendationService.getAllRecommendations(mockClient))
                .thenReturn(List.of(mockRecommendation));

        ResponseEntity<List<RecommendationResponseDTO>> response =
                recommendationController.getAllRecommendations(mockClient);

        assertNotNull(response);
        List<RecommendationResponseDTO> result = response.getBody();
        assertNotNull(result, "Response body ne doit pas être null");
        assertEquals(1, result.size());
        assertEquals(mockRecommendation.getId(), result.get(0).getId());
        verify(recommendationService).getAllRecommendations(mockClient);
    }

    @Test
    void testGetRecommendation() {
        when(recommendationService.getRecommendation(recommendationId))
                .thenReturn(mockRecommendation);

        ResponseEntity<RecommendationResponseDTO> response =
                recommendationController.getRecommendation(recommendationId);

        assertNotNull(response);
        RecommendationResponseDTO result = response.getBody();
        assertNotNull(result, "Response body ne doit pas être null");
        assertEquals(recommendationId, result.getId());
        verify(recommendationService).getRecommendation(recommendationId);
    }

    @Test
    void testDeleteRecommendation() {
        doNothing().when(recommendationService).deleteRecommendation(recommendationId, mockClient);

        ResponseEntity<?> response =
                recommendationController.deleteRecommendation(recommendationId, mockClient);

        assertNotNull(response);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body, "Response body ne doit pas être null");
        assertEquals("Recommandation supprimée avec succès.", body.get("message"));
        verify(recommendationService).deleteRecommendation(recommendationId, mockClient);
    }
}
