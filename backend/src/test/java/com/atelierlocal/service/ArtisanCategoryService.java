package com.atelierlocal.service;

import com.atelierlocal.dto.ArtisanCategoryRequestDTO;
import com.atelierlocal.dto.ArtisanCategoryResponseDTO;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.EventCategory;
import com.atelierlocal.repository.ArtisanCategoryRepo;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArtisanCategoryServiceTest {

    @Mock
    private ArtisanCategoryRepo artisanCategoryRepo;

    @InjectMocks
    private ArtisanCategoryService artisanCategoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- CREATE ---
    @Test
    void createArtisanCategory_ShouldReturnResponseDTO_WhenValidInput() {
        ArtisanCategoryRequestDTO request = new ArtisanCategoryRequestDTO();
        request.setName("Menuiserie");
        request.setDescription("Travaux du bois");

        ArtisanCategory savedEntity = new ArtisanCategory();
        savedEntity.setId(UUID.randomUUID());
        savedEntity.setName("Menuiserie");
        savedEntity.setDescription("Travaux du bois");

        when(artisanCategoryRepo.save(any(ArtisanCategory.class))).thenReturn(savedEntity);

        ArtisanCategoryResponseDTO response = artisanCategoryService.createArtisanCategory(request);

        assertNotNull(response);
        assertEquals("Menuiserie", response.getName());
        verify(artisanCategoryRepo, times(1)).save(any(ArtisanCategory.class));
    }

    @Test
    void createArtisanCategory_ShouldThrow_WhenNameIsBlank() {
        ArtisanCategoryRequestDTO request = new ArtisanCategoryRequestDTO();
        request.setName("");
        request.setDescription("desc");

        assertThrows(IllegalArgumentException.class,
                () -> artisanCategoryService.createArtisanCategory(request));
    }

    @Test
    void createArtisanCategory_ShouldThrow_WhenDescriptionIsBlank() {
        ArtisanCategoryRequestDTO request = new ArtisanCategoryRequestDTO();
        request.setName("Nom");
        request.setDescription("   ");

        assertThrows(IllegalArgumentException.class,
                () -> artisanCategoryService.createArtisanCategory(request));
    }

    // --- DELETE ---
    @Test
    void deleteArtisanCategory_ShouldDelete_WhenIdExists() {
        UUID id = UUID.randomUUID();
        ArtisanCategory entity = new ArtisanCategory();
        entity.setId(id);

        when(artisanCategoryRepo.findById(id)).thenReturn(Optional.of(entity));

        artisanCategoryService.deleteArtisanCategory(id);

        verify(artisanCategoryRepo, times(1)).delete(entity);
    }

    @Test
    void deleteArtisanCategory_ShouldThrow_WhenIdNotFound() {
        UUID id = UUID.randomUUID();
        when(artisanCategoryRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> artisanCategoryService.deleteArtisanCategory(id));
    }

    // --- UPDATE ---
    @Test
    void updateArtisanCategory_ShouldUpdateFields() {
        UUID id = UUID.randomUUID();
        ArtisanCategory entity = new ArtisanCategory();
        entity.setId(id);
        entity.setName("Ancien nom");
        entity.setDescription("Ancienne desc");

        when(artisanCategoryRepo.findById(id)).thenReturn(Optional.of(entity));
        when(artisanCategoryRepo.save(any(ArtisanCategory.class))).thenReturn(entity);

        ArtisanCategoryRequestDTO request = new ArtisanCategoryRequestDTO();
        request.setName("Nouveau nom");
        request.setDescription("Nouvelle desc");

        ArtisanCategoryResponseDTO response = artisanCategoryService.updateArtisanCategory(id, request);

        assertEquals("Nouveau nom", response.getName());
        assertEquals("Nouvelle desc", response.getDescription());
    }

    @Test
    void updateArtisanCategory_ShouldThrow_WhenNotFound() {
        UUID id = UUID.randomUUID();
        when(artisanCategoryRepo.findById(id)).thenReturn(Optional.empty());

        ArtisanCategoryRequestDTO request = new ArtisanCategoryRequestDTO();
        assertThrows(EntityNotFoundException.class,
                () -> artisanCategoryService.updateArtisanCategory(id, request));
    }

    // --- GET ALL ---
    @Test
    void getAllArtisanCategory_ShouldReturnList() {
        ArtisanCategory entity1 = new ArtisanCategory();
        entity1.setId(UUID.randomUUID());
        entity1.setName("Cat1");
        entity1.setDescription("Desc1");

        when(artisanCategoryRepo.findAll()).thenReturn(List.of(entity1));

        List<ArtisanCategoryResponseDTO> result = artisanCategoryService.getAllArtisanCategory();

        assertEquals(1, result.size());
        assertEquals("Cat1", result.get(0).getName());
    }

    @Test
    void getAllArtisanCategory_ShouldThrow_WhenEmpty() {
        when(artisanCategoryRepo.findAll()).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class,
                () -> artisanCategoryService.getAllArtisanCategory());
    }

    // --- GET BY ID ---
    @Test
    void getArtisanCategoryById_ShouldReturnDTO_WhenExists() {
        UUID id = UUID.randomUUID();
        ArtisanCategory entity = new ArtisanCategory();
        entity.setId(id);
        entity.setName("Nom");
        entity.setDescription("Desc");

        when(artisanCategoryRepo.findById(id)).thenReturn(Optional.of(entity));

        ArtisanCategoryResponseDTO response = artisanCategoryService.getArtisanCategoryById(id);

        assertEquals("Nom", response.getName());
    }

    @Test
    void getArtisanCategoryById_ShouldThrow_WhenNotFound() {
        UUID id = UUID.randomUUID();
        when(artisanCategoryRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> artisanCategoryService.getArtisanCategoryById(id));
    }

    // --- GET BY EVENT ---
    @Test
    void getArtisanCategoriesByEvent_ShouldReturnList() {
        EventCategory event = new EventCategory();

        ArtisanCategory entity = new ArtisanCategory();
        entity.setId(UUID.randomUUID());
        entity.setName("EventCat");
        entity.setDescription("EventDesc");

        when(artisanCategoryRepo.findByEventCategories(event)).thenReturn(List.of(entity));

        List<ArtisanCategoryResponseDTO> result = artisanCategoryService.getArtisanCategoriesByEvent(event);

        assertEquals(1, result.size());
        assertEquals("EventCat", result.get(0).getName());
    }
}
