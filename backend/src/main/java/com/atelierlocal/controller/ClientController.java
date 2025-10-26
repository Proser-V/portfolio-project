package com.atelierlocal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.atelierlocal.dto.AskingResponseDTO;
import com.atelierlocal.dto.ClientRequestDTO;
import com.atelierlocal.dto.ClientResponseDTO;
import com.atelierlocal.model.Client;
import com.atelierlocal.service.AskingService;
import com.atelierlocal.service.ClientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Contrôleur REST pour la gestion des clients.
 * Permet la création, mise à jour, suppression et consultation des clients,
 * ainsi que la récupération des demandes (askings) associées.
 */
@RestController
@RequestMapping("/api/clients")
@Tag(name = "Clients", description = "API pour la gestion des clients")
public class ClientController {

    private final ClientService clientService;
    private final AskingService askingService;

    /**
     * Constructeur avec injection des services nécessaires.
     * 
     * @param clientService service de gestion des clients
     * @param askingService service de gestion des demandes (askings)
     */
    public ClientController(ClientService clientService, AskingService askingService) {
        this.clientService = clientService;
        this.askingService = askingService;
    }

    // --------------------
    // ENREGISTREMENT
    // --------------------

    /**
     * Enregistrement d'un nouveau client.
     * Accessible à tous.
     * 
     * @param request données du client
     * @param avatar image optionnelle de l'avatar
     * @return ResponseEntity contenant les informations du client créé
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Enregistrement d'un nouveau client", description = "Création d'un nouveau client via les données entrées")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide (données manquantes ou incorrectes)"),
        @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<ClientResponseDTO> registerClient(
            @Valid 
            @RequestPart("client") ClientRequestDTO request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        request.setAvatar(avatar);
        ClientResponseDTO clientDto = clientService.createClient(request);
        return ResponseEntity.status(201).body(clientDto);
    }

    /**
     * Création d'un administrateur par un admin existant.
     * Accessible uniquement aux ADMIN.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/admin/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Création d'un nouvel administrateur", description = "Création d'un admin")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide (données manquantes ou incorrectes)"),
        @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<ClientResponseDTO> createAdmin(
        @Valid @RequestPart("client") ClientRequestDTO request,
        @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
            request.setAvatar(avatar);
            ClientResponseDTO clientDto = clientService.createAdmin(request);
            return ResponseEntity.status(201).body(clientDto);
    }

    // --------------------
    // PROFIL ET CONSULTATION
    // --------------------

    /**
     * Récupération du profil du client connecté.
     * Accessible aux CLIENT et ADMIN.
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<ClientResponseDTO> getCurrentUser(@AuthenticationPrincipal Client currentClient) {
        ClientResponseDTO clientDTO = clientService.getClientById(currentClient.getId());
        return ResponseEntity.ok(clientDTO);
    }

    /**
     * Liste de tous les clients.
     * Accessible uniquement aux ADMIN.
     */
    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClientResponseDTO>> getAllClients(@AuthenticationPrincipal Client currentClient) {
        List<ClientResponseDTO> allClients = clientService.getAllClients(currentClient);
        return ResponseEntity.ok(allClients);
    }

    /**
     * Détails d'un client par son ID.
     * Accessible au client lui-même ou aux ADMIN.
     */
    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<ClientResponseDTO> getClientByID(@PathVariable UUID id) {
        ClientResponseDTO client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    /**
     * Récupération des demandes (askings) d'un client.
     * Accessible au client lui-même ou aux ADMIN.
     */
    @GetMapping("/{id}/askings")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<List<AskingResponseDTO>> getAskingsByClient(@PathVariable UUID id, @AuthenticationPrincipal Client currentClient) {
        List<AskingResponseDTO> askingsByClient = askingService.getAskingsByClient(id, currentClient);
        return ResponseEntity.ok(askingsByClient);
    }

    // --------------------
    // MISE À JOUR
    // --------------------

    /**
     * Mise à jour des informations d'un client.
     * Accessible au client lui-même ou aux ADMIN.
     */
    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<ClientResponseDTO> updateClient(@Valid @PathVariable UUID id,
                                                          @ModelAttribute ClientRequestDTO requestDTO,
                                                          @AuthenticationPrincipal Client currentClient) {
        ClientResponseDTO updatedClient = clientService.updateClient(id, requestDTO, currentClient);
        return ResponseEntity.ok(updatedClient);
    }

    // --------------------
    // SUPPRESSION
    // --------------------

    /**
     * Suppression d'un client.
     * Accessible uniquement aux ADMIN.
     */
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id, @AuthenticationPrincipal Client currentClient) {
        clientService.deleteClient(id, currentClient);
        return ResponseEntity.noContent().build();
    }

    // --------------------
    // MODÉRATION
    // --------------------

    /**
     * Modération (ban) d'un client.
     * Accessible uniquement aux ADMIN.
     */
    @PatchMapping("/{id}/moderate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientResponseDTO> moderateClient(@PathVariable UUID id, @AuthenticationPrincipal Client currentClient) {
        ClientResponseDTO patchedClient = clientService.banClient(id, currentClient);
        return ResponseEntity.ok(patchedClient);
    }
}
