package com.atelierlocal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.atelierlocal.dto.ClientRegistrationRequest;
import com.atelierlocal.dto.ClientRequestDTO;
import com.atelierlocal.model.Client;
import com.atelierlocal.service.ClientService;
import com.atelierlocal.dto.ClientResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


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

    @GetMapping("/register")
    public String registerTest() {
        return "Tarte atteint";
    }

    @GetMapping("/me")
    public ResponseEntity<ClientResponseDTO> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        Client client = clientService.getClientByEmail(email);

        ClientResponseDTO clientDTO = new ClientResponseDTO(client);

        return ResponseEntity.ok(clientDTO);
    }
}
