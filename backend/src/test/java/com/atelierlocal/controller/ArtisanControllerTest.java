package com.atelierlocal.controller;

import com.atelierlocal.dto.*;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.UploadedPhoto;
import com.atelierlocal.model.User;
import com.atelierlocal.service.ArtisanService;
import com.atelierlocal.service.PortfolioService;
import com.atelierlocal.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArtisanControllerTest {

    private ArtisanController artisanController;
    private ArtisanService artisanService;
    private RecommendationService recommendationService;
    private PortfolioService portfolioService;

    private UUID artisanId;
    private Artisan artisan;
    private ArtisanResponseDTO artisanResponseDTO;
    private Client client;
    private User user;

    @BeforeEach
    void setUp() {
        artisanService = mock(ArtisanService.class);
        recommendationService = mock(RecommendationService.class);
        portfolioService = mock(PortfolioService.class);

        artisanController = new ArtisanController(artisanService, recommendationService, portfolioService);

        artisanId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        artisan = new Artisan();
        artisan.setId(artisanId);
        artisan.setEmail("artisan@example.com");
        artisan.setName("Jean Dupont");

        artisanResponseDTO = new ArtisanResponseDTO(artisan);

        client = mock(Client.class);
        user = mock(User.class);
    }

    // GET /api/artisans/me
    @Test
    void testGetCurrentUser() {
        when(artisanService.getArtisanById(artisanId)).thenReturn(artisanResponseDTO);

        ResponseEntity<ArtisanResponseDTO> response = artisanController.getCurrentUser(artisan);
        ArtisanResponseDTO result = response.getBody();

        assertNotNull(result);
        assertEquals("Jean Dupont", result.getName());
        assertEquals(200, response.getStatusCode());
        verify(artisanService).getArtisanById(artisanId);
    }

    // GET /api/artisans/{id}
    @Test
    void testGetArtisanById() {
        when(artisanService.getArtisanById(artisanId)).thenReturn(artisanResponseDTO);

        ResponseEntity<ArtisanResponseDTO> response = artisanController.getArtisanById(artisanId);
        ArtisanResponseDTO result = response.getBody();

        assertNotNull(result);
        assertEquals("artisan@example.com", result.getEmail());
        assertEquals(200, response.getStatusCode());
        verify(artisanService).getArtisanById(artisanId);
    }

    // GET /api/artisans/
    @Test
    void testGetAllArtisans() {
        when(artisanService.getAllArtisans(client)).thenReturn(List.of(artisanResponseDTO));

        ResponseEntity<List<ArtisanResponseDTO>> response = artisanController.getAllArtisans(client);
        List<ArtisanResponseDTO> result = response.getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Jean Dupont", result.get(0).getName());
        assertEquals(200, response.getStatusCode());
        verify(artisanService).getAllArtisans(client);
    }

    // POST /api/artisans/register
    @Test
    void testRegisterArtisan() {
        ArtisanRequestDTO request = new ArtisanRequestDTO();
        request.setEmail("artisan@example.com");
        request.setPassword("test123");
        MockMultipartFile avatar = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", new byte[]{1, 2, 3});
        request.setAvatar(avatar);

        when(artisanService.createArtisan(request)).thenReturn(artisanResponseDTO);

        ResponseEntity<ArtisanResponseDTO> response = artisanController.registerArtisan(request, avatar);
        ArtisanResponseDTO result = response.getBody();

        assertNotNull(result);
        assertEquals("artisan@example.com", result.getEmail());
        assertEquals(201, response.getStatusCode());
        verify(artisanService).createArtisan(request);
    }

    // PUT /api/artisans/{id}/update
    @Test
    void testUpdateArtisan() {
        ArtisanRequestDTO request = new ArtisanRequestDTO();
        request.setEmail("artisan@update.com");

        when(artisanService.updateArtisan(artisanId, request, user)).thenReturn(artisanResponseDTO);

        ResponseEntity<ArtisanResponseDTO> response = artisanController.updateArtisan(artisanId, request, user);
        ArtisanResponseDTO result = response.getBody();

        assertNotNull(result);
        assertEquals("Jean Dupont", result.getName());
        assertEquals(200, response.getStatusCode());
        verify(artisanService).updateArtisan(artisanId, request, user);
    }

    // DELETE /api/artisans/{id}/delete
    @Test
    void testDeleteArtisan() {
        doNothing().when(artisanService).deleteArtisan(artisanId, client);

        ResponseEntity<Void> response = artisanController.deleteArtisan(artisanId, client);
        assertEquals(204, response.getStatusCode());
        verify(artisanService).deleteArtisan(artisanId, client);
    }

    // POST /api/artisans/{id}/recommandation
    @Test
    void testNewRecommendation() {
        UUID clientId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID recommendationId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setClientId(clientId);

        RecommendationResponseDTO expectedResponse = new RecommendationResponseDTO(
                recommendationId,
                clientId,
                artisanId
        );

        when(recommendationService.createRecommendation(artisanId, request, client)).thenReturn(expectedResponse);

        ResponseEntity<RecommendationResponseDTO> response = artisanController.newRecommendation(artisanId, request, client);
        RecommendationResponseDTO result = response.getBody();

        assertNotNull(result);
        assertEquals(recommendationId, result.getId());
        assertEquals(clientId, result.getClientId());
        assertEquals(artisanId, result.getArtisanId());
        assertEquals(200, response.getStatusCode());
        verify(recommendationService).createRecommendation(artisanId, request, client);
    }

    // GET /api/artisans/random-top
    @Test
    void testGetRandomTopArtisans() {
        List<Artisan> artisans = List.of(artisan);
        when(artisanService.getRandomTopArtisans(3)).thenReturn(artisans);

        ResponseEntity<List<ArtisanResponseDTO>> response = artisanController.getRandomTopArtisans();
        List<ArtisanResponseDTO> result = response.getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Jean Dupont", result.get(0).getName());
        assertEquals(200, response.getStatusCode());
        verify(artisanService).getRandomTopArtisans(3);
    }

    // POST /api/artisans/{id}/portfolio/upload
    @Test
    void testUploadPortfolioPhoto() {
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});
        UploadedPhotoRequestDTO request = new UploadedPhotoRequestDTO();
        request.setFile(file);

        UploadedPhoto uploadedPhoto = new UploadedPhoto();
        uploadedPhoto.setId(UUID.randomUUID());
        uploadedPhoto.setUploadedPhotoUrl("https://s3.aws/test.jpg");
        uploadedPhoto.setArtisan(artisan);

        when(portfolioService.addPhoto(artisanId, file, artisan)).thenReturn(uploadedPhoto);

        ResponseEntity<UploadedPhotoResponseDTO> response = artisanController.uploadPortfolioPhoto(artisanId, request, artisan);
        UploadedPhotoResponseDTO result = response.getBody();

        assertNotNull(result);
        assertEquals("https://s3.aws/test.jpg", result.getFileUrl());
        assertEquals(200, response.getStatusCode());
        verify(portfolioService).addPhoto(artisanId, file, artisan);
    }

    // DELETE /api/artisans/{artisanId}/portfolio/{photoId}/delete
    @Test
    void testDeletePortfolioPhoto() {
        UUID photoId = UUID.randomUUID();
        doNothing().when(portfolioService).removePhoto(artisanId, photoId, user);

        ResponseEntity<Void> response = artisanController.deletePortfolioPhoto(artisanId, photoId, user);
        assertEquals(204, response.getStatusCode());
        verify(portfolioService).removePhoto(artisanId, photoId, user);
    }
}