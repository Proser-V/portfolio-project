/*
    Ce fichier a été créé pour faciliter l’évolution du code,
    notamment si les actions administrateur se multiplient avec la scalabilité du système.
*/

package com.atelierlocal.service;

import java.util.UUID;

import com.atelierlocal.dto.ArtisanRequestDTO;
import com.atelierlocal.dto.ArtisanResponseDTO;
import com.atelierlocal.dto.AskingRequestDTO;
import com.atelierlocal.dto.AskingResponseDTO;
import com.atelierlocal.dto.ClientRequestDTO;
import com.atelierlocal.dto.ClientResponseDTO;
import com.atelierlocal.model.Asking;

import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.ClientRepo;

public class AdminService {

    private final ClientService clientService;
    private final ArtisanService artisanService;
    private final AskingService askingService;

    // Constructeur
    public AdminService(ClientService clientService,
                        ArtisanService artisanService,
                        AskingService askingService,
                        ClientRepo clientRepo,
                        ArtisanRepo artisanRepo) {
        this.clientService = clientService;
        this.artisanService = artisanService;
        this.askingService = askingService;
    }

    // Système de gestion des utilisateurs

    public void banClient(UUID clientId) {
        clientService.banClient(clientId);
    }

    public void banArtisan(UUID artisanId) {
        artisanService.banArtisan(artisanId);
    }

    public void deleteClient(UUID clientId) {
        clientService.deleteClient(clientId);
    }

    public void deleteArtisan(UUID artisanId) {
        artisanService.deleteArtisan(artisanId);
    }

    public ClientResponseDTO updateClient(UUID clientId, ClientRequestDTO request) {
        return clientService.updateClient(clientId, request);
    }

    public ArtisanResponseDTO updateArtisan(UUID artisanId, ArtisanRequestDTO request) {
        return artisanService.updateArtisan(artisanId, request);
    }

    // Gestion des askings

    public void deleteAsking(UUID askingId) {
        askingService.deleteAsking(askingId);
    }

    public AskingResponseDTO updateAsking(UUID askingId, AskingRequestDTO request) {
        return askingService.updateAsking(askingId, request);
    }
}
