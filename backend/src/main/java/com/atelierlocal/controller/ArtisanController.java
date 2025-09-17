package com.atelierlocal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.ArtisanRegistrationRequest;
import com.atelierlocal.model.Address;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Avatar;
import com.atelierlocal.repository.ArtisanCategoryRepo;
import com.atelierlocal.service.ArtisanService;
import com.atelierlocal.service.AvatarService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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

        Address address = null;
        if (request.getAddress() != null) {
            address = new Address(
                request.getAddress().getNumber(),
                request.getAddress().getStreet(),
                request.getAddress().getPostalCode(),
                request.getAddress().getCity()
            );
        }

        ArtisanCategory category = artisanCategoryRepo.findByName(request.getCategoryName())
            .orElseThrow(() -> new IllegalArgumentException("Catégorie invalide"));

        String avatarUrl = avatarService.uploadAvatar(request.getAvatar(), null);
        Avatar avatar = new Avatar();
        avatar.setExtension(avatarService.getFileExtension(request.getAvatar()));
        avatar.setUrl(avatarUrl);

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
}
