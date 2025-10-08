package com.atelierlocal.controller;

import com.atelierlocal.dto.ArtisanCategoryRequestDTO;
import com.atelierlocal.dto.ArtisanCategoryResponseDTO;
import com.atelierlocal.dto.ArtisanResponseDTO;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Client;
import com.atelierlocal.service.ArtisanCategoryService;
import com.atelierlocal.service.ArtisanService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArtisanCategoryControllerTest {

    private ArtisanCategoryService artisanCategoryService;
    private ArtisanService artisanService;
    private ArtisanCategoryController artisanCategoryController;

    private UUID categoryId;
    private Client client;
    private Artisan artisan;
    private ArtisanCategory artisanCategory;
    private ArtisanCategoryResponseDTO categoryResponseDTO;
    private ArtisanResponseDTO artisanResponseDTO;

    @BeforeEach
    void setUp() {
        artisanCategoryService = mock(ArtisanCategoryService.class);
        artisanService = mock(ArtisanService.class);
        artisanCategoryController = new ArtisanCategoryController(artisanCategoryService, artisanService);

        categoryId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        client = new Client();
        client.setId(UUID.fromString("22222222-2222-2222-2222-222222222222"));

        artisan = new Artisan();
        artisan.setId(UUID.fromString("33333333-3333-3333-3333-333333333333"));

        artisanCategory = new ArtisanCategory();
        artisanCategory.setId(UUID.fromString("44444444-4444-4444-4444-444444444444"));

        categoryResponseDTO = new ArtisanCategoryResponseDTO(categoryId, "Plomberie", "Tout pour les plombiers");
        artisanResponseDTO = new ArtisanResponseDTO(artisan);
        artisanResponseDTO.setId(UUID.randomUUID());
        artisanResponseDTO.setName("Jean Plombier");
    }

    @Test
    void testCreateArtisanCategory() {
        ArtisanCategoryRequestDTO request = new ArtisanCategoryRequestDTO();
        request.setName("Plomberie");
        request.setDescription("Tout pour les plombiers");

        when(artisanCategoryService.createArtisanCategory(request)).thenReturn(categoryResponseDTO);

        ResponseEntity<ArtisanCategoryResponseDTO> response = artisanCategoryController.createArtisanCategory(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Plomberie", Objects.requireNonNull(response.getBody()).getName());
        verify(artisanCategoryService).createArtisanCategory(request);
    }

    @Test
    void testGetAllArtisanCategories() {
        when(artisanCategoryService.getAllArtisanCategory()).thenReturn(List.of(categoryResponseDTO));

        ResponseEntity<List<ArtisanCategoryResponseDTO>> response = artisanCategoryController.getAllArtisanCategories();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals("Plomberie", Objects.requireNonNull(response.getBody()).get(0).getName());
        verify(artisanCategoryService).getAllArtisanCategory();
    }

    @Test
    void testGetArtisanCategoryById() {
        when(artisanCategoryService.getArtisanCategoryById(categoryId)).thenReturn(categoryResponseDTO);

        ResponseEntity<ArtisanCategoryResponseDTO> response = artisanCategoryController.getArtisanCategoryById(categoryId);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(categoryId, Objects.requireNonNull(response.getBody()).getId());
        verify(artisanCategoryService).getArtisanCategoryById(categoryId);
    }

    @Test
    void testGetArtisansByCategory() {
        when(artisanService.getAllArtisansByCategory(categoryId, client)).thenReturn(List.of(artisanResponseDTO));

        ResponseEntity<List<ArtisanResponseDTO>> response = artisanCategoryController.getArtisansByCategory(categoryId, client);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals("Jean Plombier", Objects.requireNonNull(response.getBody()).get(0).getName());
        verify(artisanService).getAllArtisansByCategory(categoryId, client);
    }

    @Test
    void testUpdateArtisanCategory() {
        ArtisanCategoryRequestDTO request = new ArtisanCategoryRequestDTO();
        request.setName("Menuiserie");
        request.setDescription("Travaux bois");

        ArtisanCategoryResponseDTO updatedResponse = new ArtisanCategoryResponseDTO(categoryId, "Menuiserie", "Travaux bois");
        when(artisanCategoryService.updateArtisanCategory(categoryId, request)).thenReturn(updatedResponse);

        ResponseEntity<ArtisanCategoryResponseDTO> response = artisanCategoryController.updateArtisanCategory(categoryId, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Menuiserie", Objects.requireNonNull(response.getBody()).getName());
        verify(artisanCategoryService).updateArtisanCategory(categoryId, request);
    }

    @Test
    void testDeleteArtisanCategory() {
        ResponseEntity<Void> response = artisanCategoryController.deleteArtisanCategory(categoryId);

        assertEquals(204, response.getStatusCode().value());
        verify(artisanCategoryService).deleteArtisanCategory(categoryId);
    }
}
