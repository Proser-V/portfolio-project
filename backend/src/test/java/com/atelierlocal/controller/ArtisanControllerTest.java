package com.atelierlocal.controller;

import com.atelierlocal.dto.*;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.UploadedPhoto;
import com.atelierlocal.service.ArtisanService;
import com.atelierlocal.service.PortfolioService;
import com.atelierlocal.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    }

    // GET /api/artisans/me
    @Test
    void testGetCurrentUser() {
        when(artisanService.getArtisanById(artisanId)).thenReturn(artisanResponseDTO);

        var response = artisanController.getCurrentUser(artisan);
        ArtisanResponseDTO result = response.getBody();

        assertNotNull(result);
        assertEquals("Jean Dupont", result.getName());
        verify(artisanService).getArtisanById(artisanId);
    }

    // GET /api/artisans/{id}
    @Test
    void testGetArtisanById() {
        when(artisanService.getArtisanById(artisanId)).thenReturn(artisanResponseDTO);

        var response = artisanController.getArtisanById(artisanId);
        ArtisanResponseDTO result = response.getBody();

        assertNotNull(result);
        assertEquals("artisan@example.com", result.getEmail());
        verify(artisanService).getArtisanById(artisanId);
    }

    // GET /api/artisans/
    @Test
    void testGetAllArtisans() {
        Client client = mock(Client.class);
        when(artisanService.getAllArtisans(client)).thenReturn(List.of(artisanResponseDTO));

        var response = artisanController.getAllArtisans(client);
        List<ArtisanResponseDTO> result = response.getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Jean Dupont", result.get(0).getName());
        verify(artisanService).getAllArtisans(client);
    }

    // POST /api/artisans/register
    @Test
    void testRegisterArtisan() {
        ArtisanRequestDTO request = new ArtisanRequestDTO();
        request.setEmail("artisan@example.com");
        request.setPassword("test123");

        when(artisanService.createArtisan(request)).thenReturn(artisanResponseDTO);

        var response = artisanController.registerArtisan(request);
        ArtisanResponseDTO result = response.getBody();

        assertNotNull(result);
        assertEquals("artisan@example.com", result.getEmail());
        verify(artisanService).createArtisan(request);
    }

    // PUT /api/artisans/{id}/update
    @Test
    void testUpdateArtisan() {
        ArtisanRequestDTO request = new ArtisanRequestDTO();
        request.setEmail("artisan@update.com");

        Artisan user = mock(Artisan.class);
        when(artisanService.updateArtisan(artisanId, request, user)).thenReturn(artisanResponseDTO);

        var response = artisanController.updateArtisan(artisanId, request, user);
        ArtisanResponseDTO result = response.getBody();

        assertNotNull(result);
        assertEquals("Jean Dupont", result.getName());
        verify(artisanService).updateArtisan(artisanId, request, user);
    }

    // DELETE /api/artisans/{id}/delete
    @Test
    void testDeleteArtisan() {
        Client client = mock(Client.class);
        doNothing().when(artisanService).deleteArtisan(artisanId, client);

        artisanController.deleteArtisan(artisanId, client);
        verify(artisanService).deleteArtisan(artisanId, client);
    }

    // POST /api/artisans/{id}/recommandation
    @Test
    void testNewRecommendation() {
        // UUID fixes
        UUID fixedArtisanId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID fixedClientId  = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID fixedRecommendationId = UUID.fromString("33333333-3333-3333-3333-333333333333");


        RecommendationRequestDTO request = new RecommendationRequestDTO();
        request.setClientId(fixedClientId);

        Client client = mock(Client.class);

        // Crée un DTO avec exactement les valeurs attendues
        RecommendationResponseDTO expectedResponse = new RecommendationResponseDTO(
                fixedRecommendationId,
                fixedClientId,
                fixedArtisanId
        );

        when(recommendationService.createRecommendation(fixedArtisanId, request, client))
                .thenReturn(expectedResponse);

        var response = artisanController.newRecommendation(fixedArtisanId, request, client);
        RecommendationResponseDTO result = response.getBody();

        assertNotNull(result);
        assertEquals(fixedRecommendationId, result.getId());
        assertEquals(fixedClientId, result.getClientId());
        assertEquals(fixedArtisanId, result.getArtisanId());
        verify(recommendationService).createRecommendation(fixedArtisanId, request, client);
    }

    // POST /api/artisans/{id}/portfolio/upload
    @Test
    void testUploadPortfolioPhoto() {
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[]{1,2,3});
        UploadedPhotoRequestDTO request = new UploadedPhotoRequestDTO();
        request.setFile(file);

        UploadedPhoto uploadedPhoto = new UploadedPhoto();
        uploadedPhoto.setId(UUID.randomUUID());
        uploadedPhoto.setUploadedPhotoUrl("https://s3.aws/test.jpg");
        uploadedPhoto.setArtisan(artisan);

        Artisan artisan = mock(Artisan.class);
        when(portfolioService.addPhoto(artisanId, file, artisan)).thenReturn(uploadedPhoto);

        var response = artisanController.uploadPortfolioPhoto(artisanId, request, artisan);
        UploadedPhotoResponseDTO result = response.getBody();

        assertNotNull(result);
        assertEquals("https://s3.aws/test.jpg", result.getFileUrl());
        verify(portfolioService).addPhoto(artisanId, file, artisan);
    }

    // DELETE /api/artisans/{artisan.id}/portfolio/{photo.id}/delete
    @Test
    void testDeletePortfolioPhoto() {
        UUID photoId = UUID.randomUUID();
        Artisan user = mock(Artisan.class);
        doNothing().when(portfolioService).removePhoto(artisanId, photoId, user);

        artisanController.deletePortfolioPhoto(artisanId, photoId, user);
        verify(portfolioService).removePhoto(artisanId, photoId, user);
    }
}
