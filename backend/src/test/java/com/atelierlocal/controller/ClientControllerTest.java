package com.atelierlocal.controller;

import com.atelierlocal.dto.*;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Asking;
import com.atelierlocal.model.Client;
import com.atelierlocal.service.AskingService;
import com.atelierlocal.service.ClientService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

class ClientControllerTest {

    private ClientController clientController;
    private ClientService clientService;
    private AskingService askingService;

    private UUID clientId;
    private UUID askingId;
    private UUID artisanCategoryId;
    private Client client;
    private ClientResponseDTO clientResponseDTO;

    @BeforeEach
    void setUp() {
        clientService = mock(ClientService.class);
        askingService = mock(AskingService.class);
        clientController = new ClientController(clientService, askingService);

        clientId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        askingId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        artisanCategoryId = UUID.fromString("44444444-4444-4444-4444-444444444444");

        client = new Client();
        client.setId(clientId);
        client.setEmail("client@example.com");
        client.setFirstName("Alice");
        client.setLastName("Martin");

        clientResponseDTO = new ClientResponseDTO(client);
    }

    // POST /api/clients/register
    @Test
    void testRegisterClient() {
        ClientRequestDTO request = new ClientRequestDTO();
        request.setEmail("client@example.com");
        request.setPassword("test123");
        MockMultipartFile avatar = new MockMultipartFile(
            "avatar", "photo.jpg", "image/jpeg", "fake image content".getBytes()
        );

        when(clientService.createClient(request)).thenReturn(clientResponseDTO);

        ResponseEntity<ClientResponseDTO> response = clientController.registerClient(request, avatar);
        ClientResponseDTO result = response.getBody();

        assertNotNull(result);
        assertEquals("client@example.com", result.getEmail());
        verify(clientService).createClient(request);
    }

    // GET /api/clients/me
    @Test
    void testGetCurrentUser() {
        Client currentClient = new Client();
        currentClient.setId(clientId);
        currentClient.setEmail("client@example.com");

        when(clientService.getClientById(clientId)).thenReturn(clientResponseDTO);

        ResponseEntity<ClientResponseDTO> response = clientController.getCurrentUser(currentClient);
        ClientResponseDTO result = response.getBody();

        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
        assertEquals("Martin", result.getLastName());
        verify(clientService).getClientById(clientId);
    }


    // GET /api/clients/
    @Test
    void testGetAllClients() {
        when(clientService.getAllClients(client)).thenReturn(List.of(clientResponseDTO));

        ResponseEntity<List<ClientResponseDTO>> response = clientController.getAllClients(client);
        List<ClientResponseDTO> result = response.getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("client@example.com", result.get(0).getEmail());
        verify(clientService).getAllClients(client);
    }

    // GET /api/clients/{id}
    @Test
    void testGetClientById() {
        when(clientService.getClientById(clientId)).thenReturn(clientResponseDTO);

        ResponseEntity<ClientResponseDTO> response = clientController.getClientByID(clientId);
        ClientResponseDTO result = response.getBody();

        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
        assertEquals("Martin", result.getLastName());
        verify(clientService).getClientById(clientId);
    }

    // GET /api/clients/{id}/askings
    @Test
    void testGetAskingsByClient() {
        Client client = new Client();
        client.setId(clientId);

        ArtisanCategory artisanCategory = new ArtisanCategory();
        artisanCategory.setId(artisanCategoryId);

        Asking asking = new Asking();
        asking.setId(askingId);
        asking.setClient(client);
        asking.setTitle("Demande test");
        asking.setContent("Contenu test");
        asking.setArtisanCategory(artisanCategory);

        AskingResponseDTO askingDTO = new AskingResponseDTO(asking);
        askingDTO.setTitle("Besoin d'un fleuriste");
        when(askingService.getAskingsByClient(clientId, client)).thenReturn(List.of(askingDTO));

        ResponseEntity<List<AskingResponseDTO>> response = clientController.getAskingsByClient(clientId, client);
        List<AskingResponseDTO> result = response.getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Besoin d'un fleuriste", result.get(0).getTitle());
        verify(askingService).getAskingsByClient(clientId, client);
    }

    // PUT /api/clients/{id}/update
    @Test
    void testUpdateClient() {
        ClientRequestDTO request = new ClientRequestDTO();
        request.setEmail("update@example.com");

        when(clientService.updateClient(clientId, request, client)).thenReturn(clientResponseDTO);

        ResponseEntity<ClientResponseDTO> response = clientController.updateClient(clientId, request, client);
        ClientResponseDTO result = response.getBody();

        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
        assertEquals("Martin", result.getLastName());
        verify(clientService).updateClient(clientId, request, client);
    }

    // DELETE /api/clients/{id}/delete
    @Test
    void testDeleteClient() {
        doNothing().when(clientService).deleteClient(clientId, client);

        ResponseEntity<Void> response = clientController.deleteClient(clientId, client);

        assertEquals(204, response.getStatusCode().value());
        verify(clientService).deleteClient(clientId, client);
    }
}
