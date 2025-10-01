package com.atelierlocal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.AskingResponseDTO;
import com.atelierlocal.dto.ClientRequestDTO;
import com.atelierlocal.service.AskingService;
import com.atelierlocal.service.ClientService;
import com.atelierlocal.dto.ClientResponseDTO;
import com.atelierlocal.model.Client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api/clients")
@Tag(name = "Clients", description = "API pour la création des clients")
public class ClientController {
    private final ClientService clientService;
    private final AskingService askingService;

    public ClientController(ClientService clientService, AskingService askingService) {
        this.clientService = clientService;
        this.askingService = askingService;
    }

    @PostMapping("/register")
    @Operation(summary = "Enregistrement d'un nouveau client", description = "Création d'un nouveau client via les données entrées")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide (données manquantes ou incorrectes)"),
        @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<ClientResponseDTO> registerClient(@Valid @RequestBody ClientRequestDTO request) {
        ClientResponseDTO clientDto = clientService.createClient(request);
            return ResponseEntity.status(201).body(clientDto);
    }

    @GetMapping("/me")
    public ResponseEntity<ClientResponseDTO> getCurrentUser(Authentication authentication, @AuthenticationPrincipal Client currentClient) {
        String email = authentication.getName();
        ClientResponseDTO clientDTO = clientService.getClientByEmail(email, currentClient);
        return ResponseEntity.ok(clientDTO);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClientResponseDTO>> getAllClients(@AuthenticationPrincipal Client currentClient) {
        List<ClientResponseDTO> allClients = clientService.getAllClients(currentClient);
        return ResponseEntity.ok(allClients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClientByID(@PathVariable UUID id) {
        ClientResponseDTO client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/{id}/askings")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<List<AskingResponseDTO>> getAskingsByClient(@PathVariable UUID id, @AuthenticationPrincipal Client currentClient) {
        List<AskingResponseDTO> askingsByClient = askingService.getAskingsByClient(id, currentClient);
        return ResponseEntity.ok(askingsByClient);
    }


    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<ClientResponseDTO> updateClient(@Valid @PathVariable UUID id, @RequestBody ClientRequestDTO requestDTO, @AuthenticationPrincipal Client currentClient) {
        ClientResponseDTO updatedClient = clientService.updateClient(id, requestDTO, currentClient);
        return ResponseEntity.ok(updatedClient);
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id, @AuthenticationPrincipal Client currentClient) {
        clientService.deleteClient(id, currentClient);
        return ResponseEntity.noContent().build();
    }
}
