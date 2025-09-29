package com.atelierlocal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.ArtisanResponseDTO;
import com.atelierlocal.dto.AskingRequestDTO;
import com.atelierlocal.dto.AskingResponseDTO;
import com.atelierlocal.model.AskingStatus;
import com.atelierlocal.service.AskingService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/askings")
@Tag(name = "Askings", description = "Définition du controlleur des askings")
public class AskingController {
    private final AskingService askingService;
    
    public AskingController(AskingService askingService) {
        this.askingService = askingService;
    }

    @PostMapping("/creation")
    public ResponseEntity<AskingResponseDTO> createAsking(@RequestBody AskingRequestDTO request) {
        AskingResponseDTO newAsking = askingService.createAsking(request);
        return ResponseEntity.ok(newAsking);
    }

    @GetMapping("/")
    public ResponseEntity<List<AskingResponseDTO>> getAllAskings() {
        List<AskingResponseDTO> allAskings = askingService.getAllAskings();
        return ResponseEntity.ok(allAskings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AskingResponseDTO> getAskingById(@PathVariable UUID id) {
        AskingResponseDTO asking = askingService.getAskingById(id);
        return ResponseEntity.ok(asking);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<AskingResponseDTO> updateAsking(@PathVariable UUID id, @RequestBody AskingRequestDTO request) {
        AskingResponseDTO updatedAsking = askingService.updateAsking(id, request);
        return ResponseEntity.ok(updatedAsking);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteAsking(@PathVariable UUID id) {
        askingService.deleteAsking(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AskingResponseDTO> updateStatus(@PathVariable UUID id, @RequestParam AskingStatus status, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AskingResponseDTO updated = askingService.patchAskingStatus(id, status, userDetails);
        return ResponseEntity.ok(updated);
    }
}

