package com.atelierlocal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.AskingResponseDTO;
import com.atelierlocal.dto.ClientRequestDTO;
import com.atelierlocal.service.ClientService;
import com.atelierlocal.dto.ClientResponseDTO;

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

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/register")
    @Operation(summary = "Enregistrement d'un nouveau client", description = "Création d'un nouveau client via les données entrées")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide (données manquantes ou incorrectes)"),
        @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<ClientResponseDTO> registerClient(@Valid @ModelAttribute ClientRequestDTO request) {
        ClientResponseDTO clientDto = clientService.createClient(request);
            return ResponseEntity.status(201).body(clientDto);
    }

    @GetMapping("/me")
    public ResponseEntity<ClientResponseDTO> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        ClientResponseDTO clientDTO = clientService.getClientByEmail(email);
        return ResponseEntity.ok(clientDTO);
    }

    @GetMapping("/")
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        List<ClientResponseDTO> allClients = clientService.getAllClients();
        return ResponseEntity.ok(allClients);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClientByID(@PathVariable UUID id) {
        ClientResponseDTO client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/{id}/askings")
    public ResponseEntity<List<AskingResponseDTO>> getAskingsByClient(@PathVariable UUID id) {
        List<AskingResponseDTO> askingsByClient = clientService.getAskingsByClient(id);
        return ResponseEntity.ok(askingsByClient);
    }
    

    @PutMapping("/{id}/update")
    public ResponseEntity<ClientResponseDTO> updateClient(@PathVariable UUID id, @RequestBody ClientRequestDTO requestDTO) {
        ClientResponseDTO updatedClient = clientService.updateClient(id, requestDTO);
        return ResponseEntity.ok(updatedClient);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
