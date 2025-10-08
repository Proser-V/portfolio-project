package com.atelierlocal.controller;

import com.atelierlocal.dto.AskingRequestDTO;
import com.atelierlocal.dto.AskingResponseDTO;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Asking;
import com.atelierlocal.model.AskingStatus;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.UserRole;
import com.atelierlocal.repository.AskingRepo;
import com.atelierlocal.service.AskingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import jakarta.persistence.EntityNotFoundException;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AskingControllerTest {

    private AskingService askingService;
    private AskingRepo askingRepo;
    private AskingController askingController;

    private UUID askingId;
    private UUID clientId;
    private UUID artisanCategoryId;
    private ArtisanCategory artisanCategory;
    private Client client;
    private Asking asking;
    private AskingResponseDTO askingResponseDTO;

    @BeforeEach
    void setUp() {
        askingService = mock(AskingService.class);
        askingRepo = mock(AskingRepo.class);
        askingController = new AskingController(askingService, askingRepo);

        askingId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        clientId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        artisanCategoryId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        client = new Client();
        client.setId(clientId);
        client.setUserRole(UserRole.CLIENT);

        artisanCategory = new ArtisanCategory();
        artisanCategory.setId(artisanCategoryId);

        asking = new Asking();
        asking.setId(askingId);
        asking.setClient(client);
        asking.setArtisanCategory(artisanCategory);

        askingResponseDTO = new AskingResponseDTO(asking);
    }

    @Test
    void testCreateAsking() {
        AskingRequestDTO request = new AskingRequestDTO();
        when(askingService.createAsking(request, client)).thenReturn(askingResponseDTO);

        ResponseEntity<AskingResponseDTO> response = askingController.createAsking(request, client);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(askingId, Objects.requireNonNull(response.getBody()).getId());
        verify(askingService).createAsking(request, client);
    }

    @Test
    void testGetAllAskings() {
        when(askingService.getAllAskings(client)).thenReturn(List.of(askingResponseDTO));

        ResponseEntity<List<AskingResponseDTO>> response = askingController.getAllAskings(client);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        verify(askingService).getAllAskings(client);
    }

    @Test
    void testGetAskingById() {
        when(askingService.getAskingById(askingId)).thenReturn(askingResponseDTO);

        ResponseEntity<AskingResponseDTO> response = askingController.getAskingById(askingId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(askingId, Objects.requireNonNull(response.getBody()).getId());
        verify(askingService).getAskingById(askingId);
    }

    @Test
    void testUpdateAsking_AsOwner() throws Exception {
        AskingRequestDTO request = new AskingRequestDTO();

        when(askingRepo.findById(askingId)).thenReturn(Optional.of(asking));
        when(askingService.updateAsking(askingId, request, client)).thenReturn(askingResponseDTO);

        ResponseEntity<AskingResponseDTO> response = askingController.updateAsking(askingId, request, client);

        assertEquals(200, response.getStatusCode().value());
        verify(askingService).updateAsking(askingId, request, client);
    }

    @Test
    void testUpdateAsking_AsAdmin() throws Exception {
        AskingRequestDTO request = new AskingRequestDTO();

        Client admin = new Client();
        admin.setId(UUID.randomUUID());
        admin.setUserRole(UserRole.ADMIN);

        when(askingRepo.findById(askingId)).thenReturn(Optional.of(asking));
        when(askingService.updateAsking(askingId, request, admin)).thenReturn(askingResponseDTO);

        ResponseEntity<AskingResponseDTO> response = askingController.updateAsking(askingId, request, admin);

        assertEquals(200, response.getStatusCode().value());
        verify(askingService).updateAsking(askingId, request, admin);
    }

    @Test
    void testUpdateAsking_AccessDenied() {
        AskingRequestDTO request = new AskingRequestDTO();

        Client otherClient = new Client();
        otherClient.setId(UUID.randomUUID());
        otherClient.setUserRole(UserRole.CLIENT);

        when(askingRepo.findById(askingId)).thenReturn(Optional.of(asking));

        assertThrows(AccessDeniedException.class, () -> {
            askingController.updateAsking(askingId, request, otherClient);
        });
    }

    @Test
    void testUpdateAsking_NotFound() {
        AskingRequestDTO request = new AskingRequestDTO();
        when(askingRepo.findById(askingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            askingController.updateAsking(askingId, request, client);
        });
    }

    @Test
    void testDeleteAsking() {
        ResponseEntity<Void> response = askingController.deleteAsking(askingId, client);

        assertEquals(204, response.getStatusCode().value());
        verify(askingService).deleteAsking(askingId, client);
    }

    @Test
    void testPatchStatus() {
        AskingStatus newStatus = AskingStatus.PENDING;
        when(askingService.patchAskingStatus(askingId, newStatus, client)).thenReturn(askingResponseDTO);

        ResponseEntity<AskingResponseDTO> response = askingController.updateStatus(askingId, newStatus, client);

        assertEquals(200, response.getStatusCode().value());
        verify(askingService).patchAskingStatus(askingId, newStatus, client);
    }
}
