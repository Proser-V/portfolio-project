package com.atelierlocal.service;

import com.atelierlocal.dto.AskingRequestDTO;
import com.atelierlocal.dto.AskingResponseDTO;
import com.atelierlocal.model.*;
import com.atelierlocal.repository.*;
import com.atelierlocal.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AskingServiceTest {

    @Mock
    private AskingRepo askingRepo;
    @Mock
    private ArtisanCategoryRepo artisanCategoryRepo;
    @Mock
    private ClientRepo clientRepo;
    @Mock
    private EventCategoryRepo eventCategoryRepo;
    @Mock
    private SecurityService securityService;

    @InjectMocks
    private AskingService askingService;

    private Client client;
    private ArtisanCategory category;
    private EventCategory eventCategory;
    private Asking asking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        client = new Client();
        client.setId(UUID.randomUUID());

        category = new ArtisanCategory();
        category.setId(UUID.randomUUID());
        category.setName("Plombier");

        eventCategory = new EventCategory();
        eventCategory.setId(UUID.randomUUID());
        eventCategory.setName("Mariage");

        asking = new Asking();
        asking.setId(UUID.randomUUID());
        asking.setClient(client);
        asking.setTitle("Besoin d’un artisan");
        asking.setContent("Description");
        asking.setStatus(AskingStatus.PENDING);
        asking.setArtisanCategory(category);
    }

    // -------------------
    // createAsking
    // -------------------

    @Test
    void testCreateAsking_success() {
        AskingRequestDTO dto = new AskingRequestDTO();
        dto.setClientId(client.getId());
        dto.setTitle("Titre");
        dto.setContent("Contenu");
        dto.setArtisanCategoryId(category.getId());
        dto.setEventCategoryId(eventCategory.getId());
        dto.setEventDate(LocalDateTime.now());
        dto.setEventLocalisation("Paris");

        when(clientRepo.findById(client.getId())).thenReturn(Optional.of(client));
        when(artisanCategoryRepo.findById(category.getId())).thenReturn(Optional.of(category));
        when(eventCategoryRepo.findById(eventCategory.getId())).thenReturn(Optional.of(eventCategory));
        when(askingRepo.save(any(Asking.class))).thenAnswer(inv -> {
            Asking a = inv.getArgument(0);
            a.setId(UUID.randomUUID());
            return a;
        });

        AskingResponseDTO response = askingService.createAsking(dto, client);

        assertNotNull(response);
        assertEquals("Titre", response.getTitle());
        verify(securityService).checkClientOnly(client);
        verify(askingRepo).save(any(Asking.class));
    }

    @Test
    void testCreateAsking_missingTitle_throws() {
        AskingRequestDTO dto = new AskingRequestDTO();
        dto.setTitle("");
        dto.setContent("ok");
        dto.setClientId(client.getId());
        dto.setArtisanCategoryId(category.getId());

        assertThrows(IllegalArgumentException.class, () -> askingService.createAsking(dto, client));
    }

    @Test
    void testCreateAsking_invalidClient_throws() {
        AskingRequestDTO dto = new AskingRequestDTO();
        dto.setTitle("ok");
        dto.setContent("ok");
        dto.setClientId(UUID.randomUUID());
        dto.setArtisanCategoryId(category.getId());

        when(clientRepo.findById(any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> askingService.createAsking(dto, client));
    }

    // -------------------
    // patchAskingStatus
    // -------------------

    @Test
    void testPatchAskingStatus_success() {
        when(askingRepo.findById(asking.getId())).thenReturn(Optional.of(asking));
        when(askingRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AskingResponseDTO response = askingService.patchAskingStatus(
            asking.getId(), AskingStatus.DONE, client
        );

        assertEquals(AskingStatus.DONE, response.getStatus());
        verify(securityService).checkClientOwnershipOrAdmin(client, asking.getClient().getId());
    }

    @Test
    void testPatchAskingStatus_invalidTransition_throws() {
        asking.setStatus(AskingStatus.DONE);
        when(askingRepo.findById(asking.getId())).thenReturn(Optional.of(asking));

        assertThrows(RuntimeException.class,
            () -> askingService.patchAskingStatus(asking.getId(), AskingStatus.CANCELLED, client));
    }

    @Test
    void testPatchAskingStatus_invalidNewStatus_throws() {
        when(askingRepo.findById(asking.getId())).thenReturn(Optional.of(asking));

        assertThrows(IllegalArgumentException.class,
            () -> askingService.patchAskingStatus(asking.getId(), AskingStatus.PENDING, client));
    }

    // -------------------
    // deleteAsking
    // -------------------

    @Test
    void testDeleteAsking_success() {
        when(askingRepo.findById(asking.getId())).thenReturn(Optional.of(asking));

        askingService.deleteAsking(asking.getId(), client);

        verify(securityService).checkClientOwnershipOrAdmin(client, client.getId());
    }

    @Test
    void testDeleteAsking_notFound_throws() {
        when(askingRepo.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
            () -> askingService.deleteAsking(UUID.randomUUID(), client));
    }

    // -------------------
    // updateAsking
    // -------------------

    @Test
    void testUpdateAsking_success() {
        AskingRequestDTO dto = new AskingRequestDTO();
        dto.setTitle("Nouveau titre");
        dto.setContent("Nouveau contenu");
        dto.setArtisanCategoryId(category.getId());

        when(askingRepo.findById(asking.getId())).thenReturn(Optional.of(asking));
        when(artisanCategoryRepo.findById(category.getId())).thenReturn(Optional.of(category));
        when(askingRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AskingResponseDTO response = askingService.updateAsking(asking.getId(), dto, client);

        assertEquals("Nouveau titre", response.getTitle());
        assertEquals("Nouveau contenu", response.getContent());
    }

    // -------------------
    // getAskingById
    // -------------------

    @Test
    void testGetAskingById_success() {
        when(askingRepo.findById(asking.getId())).thenReturn(Optional.of(asking));
        AskingResponseDTO response = askingService.getAskingById(asking.getId());

        assertEquals(asking.getId(), response.getId());
    }

    @Test
    void testGetAskingById_notFound_throws() {
        when(askingRepo.findById(any())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
            () -> askingService.getAskingById(UUID.randomUUID()));
    }

    // -------------------
    // getAskingsByClient
    // -------------------

    @Test
    void testGetAskingsByClient_success() {
        when(clientRepo.findById(client.getId())).thenReturn(Optional.of(client));
        when(askingRepo.findAllByClient(client)).thenReturn(List.of(asking));

        List<AskingResponseDTO> responses = askingService.getAskingsByClient(client.getId(), client);

        assertEquals(1, responses.size());
        verify(securityService).checkClientOwnershipOrAdmin(client, client.getId());
    }

    // -------------------
    // getAllAskings
    // -------------------

    @Test
    void testGetAllAskings_success() {
        User admin = mock(User.class);
        when(askingRepo.findAll()).thenReturn(List.of(asking));

        List<AskingResponseDTO> responses = askingService.getAllAskings(admin);

        assertEquals(1, responses.size());
        verify(securityService).checkAdminOnly(admin);
    }
}
