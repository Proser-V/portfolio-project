package com.atelierlocal.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.ArtisanResponseDTO;
import com.atelierlocal.dto.ArtisanRequestDTO;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.repository.ArtisanCategoryRepo;
import com.atelierlocal.service.ArtisanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/artisans")
@Tag(name = "Artisans", description = "Définition du controlleur des artisans")
public class ArtisanController {
    private final ArtisanService artisanService;
    private final ArtisanCategoryRepo artisanCategoryRepo;

    public ArtisanController(ArtisanService artisanService, ArtisanCategoryRepo artisanCategoryRepo) {
        this.artisanService = artisanService;
        this.artisanCategoryRepo = artisanCategoryRepo;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Enregistrement d'un nouvel artisan", description = "Création d'un nouvel artisan via les données entrées")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Artisan créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide (données manquantes ou incorrectes)"),
        @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<ArtisanResponseDTO> registerArtisan(@Valid @ModelAttribute ArtisanRequestDTO request) {
        ArtisanResponseDTO artisanDto = artisanService.createArtisan(request);
        return ResponseEntity.status(201).body(artisanDto);
    }

    @GetMapping("/debug/categories")
    public List<String> debugCat() {
        return artisanCategoryRepo.findAll().stream()
            .map(ArtisanCategory::getName)
            .collect(Collectors.toList());
    }

    @GetMapping("/me")
    public ResponseEntity<ArtisanResponseDTO> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        Artisan artisan = artisanService.getArtisanByEmail(email);

        ArtisanResponseDTO artisanDto = new ArtisanResponseDTO(artisan);

    return ResponseEntity.ok(artisanDto);
    }
}
