package com.atelierlocal.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.atelierlocal.dto.ArtisanDto;
import com.atelierlocal.dto.ArtisanRegistrationRequest;
import com.atelierlocal.model.Address;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Avatar;
import com.atelierlocal.repository.ArtisanCategoryRepo;
import com.atelierlocal.service.ArtisanService;
import com.atelierlocal.service.AvatarService;

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
    private final AvatarService avatarService;
    private final ArtisanCategoryRepo artisanCategoryRepo;

    public ArtisanController(ArtisanService artisanService, AvatarService avatarService, ArtisanCategoryRepo artisanCategoryRepo) {
        this.artisanService = artisanService;
        this.avatarService = avatarService;
        this.artisanCategoryRepo = artisanCategoryRepo;
    }

    @PostMapping("/register")
    @Operation(summary = "Enregistrement d'un nouvel artisan", description = "Création d'un nouvel artisan via les données entrées")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Artisan créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide (données manquantes ou incorrectes)"),
        @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<Artisan> registerArtisan(@Valid @RequestBody ArtisanRegistrationRequest request) {

        System.out.println("request.categoryName = '" + request.getCategoryName() + "'");

        Address address = null;
        if (request.getAddress() != null) {
            address = new Address(
                request.getAddress().getNumber(),
                request.getAddress().getStreet(),
                request.getAddress().getPostalCode(),
                request.getAddress().getCity()
            );
        }

        System.out.println("Input categoryName: '" + request.getCategoryName() + "'");
        artisanCategoryRepo.findAll().forEach(c -> System.out.println("DB category: '" + c.getName() + "'"));

        ArtisanCategory category = artisanCategoryRepo.findByNameIgnoreCase(request.getCategoryName())
            .orElseThrow(() -> new IllegalArgumentException("Catégorie invalide"));

        String avatarUrl = null;
        Avatar avatar = null;
        if (request.getAvatar() != null) {
            avatarUrl = avatarService.uploadAvatar(request.getAvatar(), null);
            avatar = new Avatar();
            avatar.setExtension(avatarService.getFileExtension(request.getAvatar()));
            avatar.setAvatarUrl(avatarUrl);
        }

        Artisan artisan = artisanService.createArtisan(
            request.getName(),
            request.getEmail(),
            request.getPassword(),
            request.getBio(),
            request.getPhoneNumber(),
            request.getSiret(),
            address,
            avatar,
            category
        );

        return ResponseEntity.status(201).body(artisan);
    }

    @GetMapping("/debug/categories")
    public List<String> debugCat() {
        return artisanCategoryRepo.findAll().stream()
            .map(ArtisanCategory::getName)
            .collect(Collectors.toList());
    }

    @GetMapping("/me")
    public ResponseEntity<ArtisanDto> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        Artisan artisan = artisanService.getArtisanByEmail(email);

        String avatarUrl = artisan.getAvatar() != null ? artisan.getAvatar().getAvatarUrl() : null;

        ArtisanDto artisanDto = new ArtisanDto(
            artisan.getId(),
            artisan.getEmail(),
            avatarUrl,
            artisan.getName(),
            artisan.getActivityStartDate()
        );

    return ResponseEntity.ok(artisanDto);
    }
}
