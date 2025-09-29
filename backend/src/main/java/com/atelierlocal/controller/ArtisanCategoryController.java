package com.atelierlocal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.ArtisanCategoryRequestDTO;
import com.atelierlocal.dto.ArtisanCategoryResponseDTO;
import com.atelierlocal.dto.ArtisanResponseDTO;
import com.atelierlocal.service.ArtisanCategoryService;
import com.atelierlocal.service.ArtisanService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/artisan-category")
@Tag(name = "Artisan Categories", description = "Définition du controlleur des catégories d'artisans")
public class ArtisanCategoryController {
    private ArtisanCategoryService artisanCategoryService;
    private ArtisanService artisanService;

    public ArtisanCategoryController(ArtisanCategoryService artisanCategoryService, ArtisanService artisanService) {
        this.artisanCategoryService = artisanCategoryService;
        this.artisanService = artisanService;
    }

    @PostMapping("/creation")
    public ResponseEntity<ArtisanCategoryResponseDTO> createArtisanCategory(@RequestBody ArtisanCategoryRequestDTO request) {
        ArtisanCategoryResponseDTO newArtisanCategory = artisanCategoryService.createArtisanCategory(request);
        return ResponseEntity.ok(newArtisanCategory);
    }
    
    @GetMapping("/")
    public ResponseEntity<List<ArtisanCategoryResponseDTO>> getAllArtisanCategories() {
        List<ArtisanCategoryResponseDTO> allArtisanCategories = artisanCategoryService.getAllArtisanCategory();
        return ResponseEntity.ok(allArtisanCategories);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ArtisanCategoryResponseDTO> getArtisanCategoryById(@PathVariable UUID id) {
        ArtisanCategoryResponseDTO artisanCategory = artisanCategoryService.getArtisanCategoryById(id);
        return ResponseEntity.ok(artisanCategory);
    }

    @GetMapping("/{id}/artisans")
    public ResponseEntity<List<ArtisanResponseDTO>> getArtisansByCategory(@PathVariable UUID id) {
        List<ArtisanResponseDTO> artisansByCategory = artisanService.getAllArtisansByCategory(id);
        return ResponseEntity.ok(artisansByCategory);
    }
    

    @PutMapping("/{id}/update")
    public ResponseEntity<ArtisanCategoryResponseDTO> updateArtisanCategory(@PathVariable UUID id, @RequestBody ArtisanCategoryRequestDTO request) {
        ArtisanCategoryResponseDTO updatedArtisanCategory = artisanCategoryService.updateArtisanCategory(id, request);
        return ResponseEntity.ok(updatedArtisanCategory);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteArtisanCategory(@PathVariable UUID id) {
        artisanCategoryService.deleteArtisanCategory(id);
        return ResponseEntity.noContent().build();
    }
}
