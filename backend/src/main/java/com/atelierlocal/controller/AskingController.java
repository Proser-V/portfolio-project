package com.atelierlocal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.AskingRequestDTO;
import com.atelierlocal.dto.AskingResponseDTO;
import com.atelierlocal.model.Asking;
import com.atelierlocal.model.AskingStatus;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.User;
import com.atelierlocal.model.UserRole;
import com.atelierlocal.repository.AskingRepo;
import com.atelierlocal.service.AskingService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.persistence.EntityNotFoundException;


@RestController
@RequestMapping("/api/askings")
@Tag(name = "Askings", description = "Définition du controlleur des askings")
public class AskingController {
    private final AskingService askingService;
    private final AskingRepo askingRepo;
    
    public AskingController(AskingService askingService, AskingRepo askingRepo) {
        this.askingService = askingService;
        this.askingRepo = askingRepo;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<AskingResponseDTO> updateAsking(
                                                @PathVariable UUID id,
                                                @RequestBody AskingRequestDTO request,
                                                @AuthenticationPrincipal Client currentClient
                                                ) throws AccessDeniedException {
        Asking asking = askingRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Demande non trouvée."));

        if (!asking.getClient().getId().equals(currentClient.getId()) || currentClient.getUserRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Vous ne pouvez pas modifier cette demande.");
        }

        AskingResponseDTO updatedAsking = askingService.updateAsking(id, request);
        return ResponseEntity.ok(updatedAsking);
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAsking(@PathVariable UUID id) {
        askingService.deleteAsking(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<AskingResponseDTO> updateStatus(
                                                @PathVariable UUID id,
                                                @RequestParam AskingStatus status,
                                                @AuthenticationPrincipal Client currentClient
                                                ) {
        AskingResponseDTO updated = askingService.patchAskingStatus(id, status, currentClient);
        return ResponseEntity.ok(updated);
    }
}

