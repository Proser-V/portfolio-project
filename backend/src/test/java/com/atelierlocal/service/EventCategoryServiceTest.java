package com.atelierlocal.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.atelierlocal.dto.EventCategoryRequestDTO;
import com.atelierlocal.dto.EventCategoryResponseDTO;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.EventCategory;
import com.atelierlocal.repository.ArtisanCategoryRepo;
import com.atelierlocal.repository.EventCategoryRepo;

class EventCategoryServiceTest {

    @Mock
    private EventCategoryRepo eventCategoryRepo;

    @Mock
    private ArtisanCategoryRepo artisanCategoryRepo;

    @InjectMocks
    private EventCategoryService eventCategoryService;

    private UUID artisanCategoryId;
    private EventCategory eventCategory;
    private ArtisanCategory artisanCategory;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        artisanCategoryId = UUID.randomUUID();

        artisanCategory = new ArtisanCategory();
        artisanCategory.setId(artisanCategoryId);
        artisanCategory.setName("Plombier");

        eventCategory = new EventCategory();
        eventCategory.setName("Rénovation");
        eventCategory.setArtisanCategoryList(List.of(artisanCategory));
    }

    // --- CREATION ---

    @Test
    void testCreateEventCategory_success() {
        EventCategoryRequestDTO request = new EventCategoryRequestDTO();
        request.setName("Plomberie & Chauffage");
        request.setArtisanCategoryList(List.of(artisanCategoryId));

        when(artisanCategoryRepo.findAllById(List.of(artisanCategoryId)))
            .thenReturn(List.of(artisanCategory));
        when(eventCategoryRepo.save(any(EventCategory.class)))
            .thenReturn(eventCategory);

        EventCategoryResponseDTO response = eventCategoryService.createEventCategory(request);

        ArgumentCaptor<EventCategory> captor = ArgumentCaptor.forClass(EventCategory.class);
        verify(eventCategoryRepo).save(captor.capture());

        EventCategory savedEntity = captor.getValue();
        assertEquals("Plomberie & Chauffage", savedEntity.getName());
        assertEquals(1, savedEntity.getArtisanCategoryList().size());
        assertEquals(artisanCategoryId, savedEntity.getArtisanCategoryList().get(0).getId());

        assertNotNull(response);
        assertEquals("Rénovation", response.getName()); // valeur mockée par save()
        assertTrue(response.getArtisanCategoryIds().contains(artisanCategoryId));
    }

    @Test
    void testCreateEventCategory_withEmptyArtisanList() {
        EventCategoryRequestDTO request = new EventCategoryRequestDTO();
        request.setName("Invalide");
        request.setArtisanCategoryList(List.of());

        when(artisanCategoryRepo.findAllById(anyList())).thenReturn(Collections.emptyList());
        when(eventCategoryRepo.save(any(EventCategory.class))).thenReturn(eventCategory);

        EventCategoryResponseDTO response = eventCategoryService.createEventCategory(request);

        assertNotNull(response);
        verify(eventCategoryRepo).save(any());
    }

    // --- MISE A JOUR ---

    @Test
    void testUpdateEventCategory_success() {
        UUID id = UUID.randomUUID();
        EventCategory existing = new EventCategory();
        existing.setName("Ancien nom");
        existing.setArtisanCategoryList(List.of());

        EventCategoryRequestDTO request = new EventCategoryRequestDTO();
        request.setName("Nouveau nom");
        request.setArtisanCategoryList(List.of(artisanCategoryId));

        when(eventCategoryRepo.findById(id)).thenReturn(Optional.of(existing));
        when(artisanCategoryRepo.findAllById(List.of(artisanCategoryId)))
            .thenReturn(List.of(artisanCategory));
        when(eventCategoryRepo.save(any(EventCategory.class)))
            .thenReturn(existing);

        EventCategoryResponseDTO response = eventCategoryService.updateEventCategory(id, request);

        ArgumentCaptor<EventCategory> captor = ArgumentCaptor.forClass(EventCategory.class);
        verify(eventCategoryRepo).save(captor.capture());
        EventCategory updated = captor.getValue();

        assertEquals("Nouveau nom", updated.getName());
        assertEquals(1, updated.getArtisanCategoryList().size());

        assertNotNull(response);
        assertEquals("Nouveau nom", response.getName());
    }

    @Test
    void testUpdateEventCategory_notFound() {
        UUID id = UUID.randomUUID();
        EventCategoryRequestDTO request = new EventCategoryRequestDTO();
        request.setName("Test");

        when(eventCategoryRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> eventCategoryService.updateEventCategory(id, request));
        verify(eventCategoryRepo, never()).save(any());
    }

    // --- SUPPRESSION ---

    @Test
    void testDeleteEventCategory_success() {
        UUID id = UUID.randomUUID();
        when(eventCategoryRepo.existsById(id)).thenReturn(true);

        eventCategoryService.deleteEventCategory(id);

        verify(eventCategoryRepo).deleteById(id);
    }

    @Test
    void testDeleteEventCategory_notFound() {
        UUID id = UUID.randomUUID();
        when(eventCategoryRepo.existsById(id)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> eventCategoryService.deleteEventCategory(id));
        verify(eventCategoryRepo, never()).deleteById(any());
    }

    // --- GET BY ID ---

    @Test
    void testGetEventCategoryById_success() {
        UUID id = UUID.randomUUID();
        when(eventCategoryRepo.findById(id)).thenReturn(Optional.of(eventCategory));

        EventCategoryResponseDTO response = eventCategoryService.getEventCategoryById(id);

        assertNotNull(response);
        assertEquals("Rénovation", response.getName());
        assertTrue(response.getArtisanCategoryIds().contains(artisanCategoryId));
    }

    @Test
    void testGetEventCategoryById_notFound() {
        UUID id = UUID.randomUUID();
        when(eventCategoryRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> eventCategoryService.getEventCategoryById(id));
    }

    // --- GET ALL ---

    @Test
    void testGetAllEventCategories() {
        when(eventCategoryRepo.findAll()).thenReturn(List.of(eventCategory));

        List<EventCategoryResponseDTO> response = eventCategoryService.getAllEventCategories();

        assertEquals(1, response.size());
        assertEquals("Rénovation", response.get(0).getName());
    }
}
