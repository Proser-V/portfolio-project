package com.atelierlocal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.AskingRequestDTO;
import com.atelierlocal.dto.AskingResponseDTO;
import com.atelierlocal.model.Asking;
import com.atelierlocal.model.AskingStatus;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.UserRole;
import com.atelierlocal.repository.AskingRepo;
import com.atelierlocal.service.AskingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/askings")
@CrossOrigin(origins = "*")
@Tag(name = "Askings", description = "API pour la gestion des demandes (askings)")
public class AskingController {
    private final AskingService askingService;
    private final AskingRepo askingRepo;

    public AskingController(AskingService askingService, AskingRepo askingRepo) {
        this.askingService = askingService;
        this.askingRepo = askingRepo;
    }

    @PostMapping("/creation")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Créer une demande", description = "Permet à un client de créer une nouvelle demande")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Demande créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide ou données manquantes"),
        @ApiResponse(responseCode = "401", description = "Client non authentifié"),
        @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    })
    public ResponseEntity<AskingResponseDTO> createAsking(
            @Valid @RequestBody AskingRequestDTO request,
            @AuthenticationPrincipal Client currentClient
    ) {
        AskingResponseDTO newAsking = askingService.createAsking(request, currentClient);
        return ResponseEntity.ok(newAsking);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister toutes les demandes", description = "Accessible uniquement aux administrateurs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des demandes récupérée"),
        @ApiResponse(responseCode = "401", description = "Admin non authentifié"),
        @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    })
    public ResponseEntity<List<AskingResponseDTO>> getAllAskings(@AuthenticationPrincipal Client currentClient) {
        List<AskingResponseDTO> allAskings = askingService.getAllAskings(currentClient);
        return ResponseEntity.ok(allAskings);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une demande par ID", description = "Permet de récupérer une demande via son identifiant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Demande récupérée avec succès"),
        @ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    public ResponseEntity<AskingResponseDTO> getAskingById(@PathVariable UUID id) {
        AskingResponseDTO asking = askingService.getAskingById(id);
        return ResponseEntity.ok(asking);
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @Operation(summary = "Mettre à jour une demande", description = "Un client peut mettre à jour sa propre demande. Admins peuvent tout modifier.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Demande mise à jour avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide"),
        @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
        @ApiResponse(responseCode = "403", description = "Droits insuffisants"),
        @ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    public ResponseEntity<AskingResponseDTO> updateAsking(
            @Valid @PathVariable UUID id,
            @RequestBody AskingRequestDTO request,
            @AuthenticationPrincipal Client currentClient
    ) throws AccessDeniedException {
        Asking asking = askingRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Demande non trouvée."));

        boolean isAdmin = currentClient.getUserRole() == UserRole.ADMIN;
        boolean isOwner = asking.getClient().getId().equals(currentClient.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Vous ne pouvez pas modifier cette demande.");
        }

        AskingResponseDTO updatedAsking = askingService.updateAsking(id, request, currentClient);
        return ResponseEntity.ok(updatedAsking);
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une demande", description = "Accessible uniquement aux administrateurs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Demande supprimée avec succès"),
        @ApiResponse(responseCode = "401", description = "Admin non authentifié"),
        @ApiResponse(responseCode = "403", description = "Droits insuffisants"),
        @ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    public ResponseEntity<Void> deleteAsking(
            @PathVariable UUID id,
            @AuthenticationPrincipal Client currentClient
    ) {
        askingService.deleteAsking(id, currentClient);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @Operation(summary = "Mettre à jour le statut d'une demande", description = "Client propriétaire ou admin peut changer le statut")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès"),
        @ApiResponse(responseCode = "400", description = "Statut invalide"),
        @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
        @ApiResponse(responseCode = "403", description = "Droits insuffisants"),
        @ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    public ResponseEntity<AskingResponseDTO> updateStatus(
            @Valid @PathVariable UUID id,
            @RequestParam AskingStatus status,
            @AuthenticationPrincipal Client currentClient
    ) {
        AskingResponseDTO updated = askingService.patchAskingStatus(id, status, currentClient);
        return ResponseEntity.ok(updated);
    }
}
