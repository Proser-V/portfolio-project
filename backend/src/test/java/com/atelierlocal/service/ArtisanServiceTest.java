package com.atelierlocal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.atelierlocal.dto.ArtisanRequestDTO;
import com.atelierlocal.dto.ArtisanResponseDTO;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.User;
import com.atelierlocal.repository.ArtisanCategoryRepo;
import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.AvatarRepo;
import com.atelierlocal.security.SecurityService;

import jakarta.persistence.EntityNotFoundException;

class ArtisanServiceTest {

    @Mock
    private PasswordService passwordService;

    @Mock
    private ArtisanRepo artisanRepo;

    @Mock
    private AvatarService avatarService;

    @Mock
    private AvatarRepo avatarRepo;

    @Mock
    private ArtisanCategoryRepo artisanCategoryRepo;

    @Mock
    private SecurityService sercurityService;

    @InjectMocks
    private ArtisanService artisanService;

    @InjectMocks
    private RecommendationService recommendationService;

    private Artisan artisan;
    private ArtisanCategory category;
    private UUID artisanId;
    private UUID categoryId;
    private Client client;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        artisanId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        category = new ArtisanCategory();
        category.setId(categoryId);
        category.setName("Plombier");

        artisan = new Artisan();
        artisan.setId(artisanId);
        artisan.setName("Jean Dupont");
        artisan.setEmail("jean@mail.com");
        artisan.setCategory(category);
        artisan.setActive(true);
        artisan.setPhotoGallery(new ArrayList<>());
    }

    // --- CREATE ---

    @Test
    void testCreateArtisan_success() {
        ArtisanRequestDTO dto = new ArtisanRequestDTO();
        dto.setName("Jean Dupont");
        dto.setEmail("jean@mail.com");
        dto.setPassword("secret");
        dto.setCategoryName("Plombier");
        dto.setLatitude(12.34);
        dto.setLongitude(56.78);

        when(artisanRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(artisanCategoryRepo.findByNameIgnoreCase("Plombier"))
                .thenReturn(Optional.of(category));
        when(passwordService.hashPassword("secret")).thenReturn("hashed");
        when(artisanRepo.save(any(Artisan.class))).thenReturn(artisan);

        ArtisanResponseDTO response = artisanService.createArtisan(dto);

        assertNotNull(response);
        assertEquals("Jean Dupont", response.getName());
        verify(artisanRepo).save(any(Artisan.class));
    }

    @Test
    void testCreateArtisan_duplicateEmail() {
        ArtisanRequestDTO dto = new ArtisanRequestDTO();
        dto.setEmail("jean@mail.com");

        when(artisanRepo.findByEmail(dto.getEmail())).thenReturn(Optional.of(artisan));

        assertThrows(IllegalArgumentException.class, () -> artisanService.createArtisan(dto));
    }

    @Test
    void testCreateArtisan_missingPassword() {
        ArtisanRequestDTO dto = new ArtisanRequestDTO();
        dto.setEmail("new@mail.com");
        dto.setName("Test");
        dto.setCategoryName("Plombier");
        dto.setLatitude(12.0);
        dto.setLongitude(34.0);

        when(artisanRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        dto.setPassword(null);
        assertThrows(IllegalArgumentException.class, () -> artisanService.createArtisan(dto));
    }

    @Test
    void testCreateArtisan_invalidCategory() {
        ArtisanRequestDTO dto = new ArtisanRequestDTO();
        dto.setEmail("new@mail.com");
        dto.setName("Test");
        dto.setPassword("pass");
        dto.setCategoryName("Invalide");
        dto.setLatitude(12.0);
        dto.setLongitude(34.0);

        when(artisanRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(artisanCategoryRepo.findByNameIgnoreCase("Invalide")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> artisanService.createArtisan(dto));
    }

    // --- DELETE ---

    @Test
    void testDeleteArtisan_success() {
        when(artisanRepo.findById(artisanId)).thenReturn(Optional.of(artisan));

        artisanService.deleteArtisan(artisanId, client);

        verify(artisanRepo).delete(artisan);
    }

    @Test
    void testDeleteArtisan_notFound() {
        when(artisanRepo.findById(artisanId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> artisanService.deleteArtisan(artisanId, client));
    }

    // --- UPDATE ---

    @Test
    void testUpdateArtisan_success() {
        ArtisanRequestDTO dto = new ArtisanRequestDTO();
        dto.setName("Nouveau Nom");
        dto.setCategoryName("Plombier");
        dto.setPassword("newpass");

        when(artisanRepo.findById(artisanId)).thenReturn(Optional.of(artisan));
        when(artisanCategoryRepo.findByNameIgnoreCase("Plombier")).thenReturn(Optional.of(category));
        when(passwordService.hashPassword("newpass")).thenReturn("hashed");
        when(artisanRepo.save(any(Artisan.class))).thenReturn(artisan);

        ArtisanResponseDTO response = artisanService.updateArtisan(artisanId, dto, user);

        assertNotNull(response);
        assertEquals("Nouveau Nom", response.getName());
        verify(artisanRepo).save(artisan);
    }

    @Test
    void testUpdateArtisan_notFound() {
        when(artisanRepo.findById(artisanId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                artisanService.updateArtisan(artisanId, new ArtisanRequestDTO(), user));
    }

    // --- GETTERS ---

    @Test
    void testGetArtisanById_success() {
        when(artisanRepo.findById(artisanId)).thenReturn(Optional.of(artisan));

        ArtisanResponseDTO response = artisanService.getArtisanById(artisanId);

        assertNotNull(response);
        assertEquals("Jean Dupont", response.getName());
    }

    @Test
    void testGetArtisanById_notFound() {
        when(artisanRepo.findById(artisanId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> artisanService.getArtisanById(artisanId));
    }

    @Test
    void testGetArtisanByEmail_success() {
        when(artisanRepo.findByEmail("jean@mail.com")).thenReturn(Optional.of(artisan));

        ArtisanResponseDTO response = artisanService.getArtisanByEmail("jean@mail.com", client);

        assertNotNull(response);
        assertEquals("Jean Dupont", response.getName());
    }

    @Test
    void testGetArtisanByEmail_notFound() {
        when(artisanRepo.findByEmail("x@mail.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> artisanService.getArtisanByEmail("x@mail.com", client));
    }

    @Test
    void testGetAllArtisans() {
        when(artisanRepo.findAll()).thenReturn(List.of(artisan));

        List<ArtisanResponseDTO> artisans = artisanService.getAllArtisans(client);

        assertEquals(1, artisans.size());
    }

    @Test
    void testGetAllArtisansByCategory_success() {
        when(artisanCategoryRepo.findById(categoryId)).thenReturn(Optional.of(category));
        when(artisanRepo.findAllByCategory(category)).thenReturn(List.of(artisan));

        List<ArtisanResponseDTO> artisans = artisanService.getAllArtisansByCategory(categoryId, client);

        assertEquals(1, artisans.size());
    }

    @Test
    void testGetAllArtisansByCategory_notFound() {
        when(artisanCategoryRepo.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                artisanService.getAllArtisansByCategory(categoryId, client));
    }

    // --- BAN ---

    @Test
    void testBanArtisan_success() {
        when(artisanRepo.findById(artisanId)).thenReturn(Optional.of(artisan));

        artisanService.banArtisan(artisanId);

        assertFalse(artisan.getActive());
        verify(artisanRepo).save(artisan);
    }

    @Test
    void testBanArtisan_notFound() {
        when(artisanRepo.findById(artisanId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> artisanService.banArtisan(artisanId));
    }
}
