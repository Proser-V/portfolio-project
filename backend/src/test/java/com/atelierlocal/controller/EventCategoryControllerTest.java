package com.atelierlocal.controller;

import com.atelierlocal.dto.EventCategoryRequestDTO;
import com.atelierlocal.dto.EventCategoryResponseDTO;
import com.atelierlocal.model.EventCategory;
import com.atelierlocal.service.EventCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventCategoryControllerTest {

    private EventCategoryController controller;
    private EventCategoryService service;

    private UUID eventCategoryId;
    private EventCategory eventCategory;
    private EventCategoryResponseDTO eventCategoryResponseDTO;

    @BeforeEach
    void setUp() {
        service = mock(EventCategoryService.class);
        controller = new EventCategoryController(service);

        eventCategoryId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        eventCategory = new EventCategory();
        eventCategory.setId(eventCategoryId);
        eventCategory.setName("Mariage");

        eventCategoryResponseDTO = new EventCategoryResponseDTO(eventCategory);
        eventCategoryResponseDTO.setId(eventCategory.getId());
        eventCategoryResponseDTO.setName(eventCategory.getName());
        eventCategoryResponseDTO.setArtisanCategoryIds(Arrays.asList(UUID.fromString("22222222-2222-2222-2222-222222222222"), UUID.fromString("33333333-3333-3333-3333-333333333333")));
    }

    @Test
    void testCreateEventCategory() {
        EventCategoryRequestDTO request = new EventCategoryRequestDTO();
        request.setName("Mariage");

        when(service.createEventCategory(request)).thenReturn(eventCategoryResponseDTO);

        ResponseEntity<EventCategoryResponseDTO> response = controller.createEventCategory(request);
        assertNotNull(response.getBody(), "Response body should not be null");

        EventCategoryResponseDTO body = Objects.requireNonNull(response.getBody(), "Response body is null");
        assertEquals("Mariage", body.getName());
        verify(service).createEventCategory(request);
    }

    @Test
    void testGetAllEventCategories() {
        when(service.getAllEventCategories()).thenReturn(List.of(eventCategoryResponseDTO));

        ResponseEntity<List<EventCategoryResponseDTO>> response = controller.getAllEventCateogries();
        assertNotNull(response.getBody());

        List<EventCategoryResponseDTO> body = Objects.requireNonNull(response.getBody(), "Response body is null");
        assertEquals(1, body.size());
        assertEquals(eventCategoryId, body.get(0).getId());
        verify(service).getAllEventCategories();
    }

    @Test
    void testGetEventCategoryById() {
        when(service.getEventCategoryById(eventCategoryId)).thenReturn(eventCategoryResponseDTO);

        ResponseEntity<EventCategoryResponseDTO> response = controller.getEventCategoryById(eventCategoryId);
        assertNotNull(response.getBody());

        EventCategoryResponseDTO body = Objects.requireNonNull(response.getBody(), "Response body is null");
        assertEquals(eventCategoryId, body.getId());
        verify(service).getEventCategoryById(eventCategoryId);
    }

    @Test
    void testUpdateEventCategory() {
        EventCategoryRequestDTO request = new EventCategoryRequestDTO();
        request.setName("Anniversaire");

        EventCategoryResponseDTO updatedDTO = new EventCategoryResponseDTO(eventCategory);
        updatedDTO.setId(eventCategoryId);
        updatedDTO.setName("Anniversaire");

        when(service.updateEventCategory(eventCategoryId, request)).thenReturn(updatedDTO);

        ResponseEntity<EventCategoryResponseDTO> response = controller.updateEventCategory(eventCategoryId, request);
        assertNotNull(response.getBody());

        EventCategoryResponseDTO body = Objects.requireNonNull(response.getBody(), "Response body is null");
        assertEquals("Anniversaire", body.getName());
        verify(service).updateEventCategory(eventCategoryId, request);
    }

    @Test
    void testDeleteEventCategory() {
        doNothing().when(service).deleteEventCategory(eventCategoryId);

        ResponseEntity<Void> response = controller.deleteEventCategory(eventCategoryId);
        assertEquals(204, response.getStatusCode().value());
        verify(service).deleteEventCategory(eventCategoryId);
    }
}
