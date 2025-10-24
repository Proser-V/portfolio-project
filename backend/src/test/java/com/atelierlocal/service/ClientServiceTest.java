package com.atelierlocal.service;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.atelierlocal.dto.ClientRequestDTO;
import com.atelierlocal.dto.ClientResponseDTO;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.UserRole;
import com.atelierlocal.repository.AvatarRepo;
import com.atelierlocal.repository.ClientRepo;
import com.atelierlocal.security.SecurityService;

public class ClientServiceTest {

    @Mock
    private PasswordService passwordService;

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private AvatarService avatarService;

    @Mock
    private AvatarRepo avatarRepo;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateClient_success() {
        ClientRequestDTO dto = new ClientRequestDTO();
        dto.setEmail("test@mail.com");
        dto.setPassword("password");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setLatitude(12.34);
        dto.setLongitude(56.78);
        dto.setRole(UserRole.CLIENT);

        when(clientRepo.findByEmail("test@mail.com")).thenReturn(Optional.empty());
        when(passwordService.hashPassword("password")).thenReturn("hashedPassword");

        Client savedClient = new Client();
        savedClient.setId(UUID.randomUUID());
        savedClient.setEmail(dto.getEmail());
        savedClient.setFirstName(dto.getFirstName());
        savedClient.setLastName(dto.getLastName());
        savedClient.setHashedPassword("hashedPassword");
        savedClient.setUserRole(UserRole.CLIENT);

        when(clientRepo.save(any(Client.class))).thenReturn(savedClient);

        ClientResponseDTO response = clientService.createClient(dto);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
    }

    @Test
    void testCreateClient_emailAlreadyExists() {
        ClientRequestDTO dto = new ClientRequestDTO();
        dto.setEmail("existing@mail.com");
        dto.setPassword("password");

        when(clientRepo.findByEmail("existing@mail.com")).thenReturn(Optional.of(new Client()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.createClient(dto);
        });

        assertEquals("Cet email est déjà enregistré.", exception.getMessage());
    }

    @Test
    void testGetClientById_success() {
        UUID clientId = UUID.randomUUID();

        Client client = new Client();
        client.setId(clientId);
        client.setEmail("test@mail.com");
        client.setFirstName("John");
        client.setLastName("Doe");

        when(clientRepo.findById(clientId)).thenReturn(Optional.of(client));

        ClientResponseDTO response = clientService.getClientById(clientId);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
    }

    @Test
    void testClientById_notFound() {
        UUID clientId = UUID.randomUUID();
        when(clientRepo.findById(clientId)).thenReturn(Optional.empty());

        Exception e = assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> {
            clientService.getClientById(clientId);
        });

        assertEquals("Utilisateur non trouvé.", e.getMessage());
    }

    @Test
    void testUpdateClient_success() {
        UUID clientId = UUID.randomUUID();

        Client existingClient = new Client();
        existingClient.setId(clientId);
        existingClient.setEmail("old@mail.com");
        existingClient.setFirstName("Old");
        existingClient.setLastName("Name");

        ClientRequestDTO dto = new ClientRequestDTO();
        dto.setFirstName("New");
        dto.setLastName("Name");

        when(clientRepo.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(clientRepo.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientResponseDTO response = clientService.updateClient(clientId, dto, existingClient);

        assertNotNull(response);
        assertEquals("New", response.getFirstName());
        assertEquals("Name", response.getLastName());
    }

    @Test
    void testDeleteClient_success() {
    UUID clientId = UUID.randomUUID();

    Client client = new Client();
    client.setId(clientId);

    Client admin = new Client();
    admin.setId(UUID.randomUUID());
    admin.setUserRole(UserRole.ADMIN);

    when(clientRepo.findById(clientId)).thenReturn(Optional.of(client));
    doNothing().when(securityService).checkAdminOnly(admin);

    clientService.deleteClient(clientId, admin);

    verify(clientRepo, times(1)).delete(client);
    }
}
