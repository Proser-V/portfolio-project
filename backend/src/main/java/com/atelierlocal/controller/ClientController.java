package com.atelierlocal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.atelierlocal.dto.ClientRegistrationRequest;
import com.atelierlocal.model.Client;
import com.atelierlocal.service.ClientService;
import com.atelierlocal.dto.ClientDto;

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
    public ResponseEntity<Client> registerClient(@Valid @RequestBody ClientRegistrationRequest request) {
        Client client = clientService.createClient(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getAddress(),
            request.getPassword(),
            request.getAvatar(),
            true,
            null
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/clients" + client.getId())
                .body(client);
    }

    @GetMapping("/register")
    public String registerTest() {
        return "Tarte atteint";
    }

    @GetMapping("/me")
    public ResponseEntity<ClientDto> getCurrentUser(@AuthenticationPrincipal Client clientDetails) {
        String avatarUrl = clientDetails.getAvatar() != null
        ? clientDetails.getAvatar().getUrl()
        : null;
        ClientDto userDto = new ClientDto(
            clientDetails.getId(),
            clientDetails.getEmail(),
            avatarUrl,
            clientDetails.getPhoneNumber(),
            clientDetails.getFirstName(),
            clientDetails.getLastName()
        );

        return ResponseEntity.ok(userDto);
    }
}
