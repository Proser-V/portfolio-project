/*
    Ce fichier a été créé pour faciliter l’évolution du code,
    notamment si les actions administrateur se multiplient avec la scalabilité du système.
*/

package com.atelierlocal.service;

import java.util.UUID;

import com.atelierlocal.model.Client;
import com.atelierlocal.dto.UpdateArtisanRequest;
import com.atelierlocal.dto.UpdateAskingRequest;
import com.atelierlocal.dto.UpdateClientRequest;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Asking;

import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.ClientRepo;

public class AdminService {

    private final ClientRepo clientRepo;
    private final ArtisanRepo artisanRepo;

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
        this.clientRepo = clientRepo;
        this.artisanRepo = artisanRepo;
    }

    // Système de gestion des utilisateurs

    public void banClient(UUID clientId) {
        Client client = clientService.getClientById(clientId);
        client.setActive(false);
        clientRepo.save(client);
    }

    public void banArtisan(UUID artisanId) {
        Artisan artisan = artisanService.getArtisanById(artisanId);
        artisan.setActive(false);
        artisanRepo.save(artisan);
    }

    public void deleteClient(UUID clientId) {
        clientService.deleteClient(clientId);
    }

    public void deleteArtisan(UUID artisanId) {
        artisanService.deleteArtisan(artisanId);
    }

    public Client updateClient(UUID clientId, UpdateClientRequest request) {
        return clientService.updateClient(clientId, request);
    }

    public Artisan updateArtisan(UUID artisanId, UpdateArtisanRequest request) {
        return artisanService.updateArtisan(artisanId, request);
    }

    // Gestion des askings

    public void deleteAsking(UUID askingId) {
        askingService.deleteAsking(askingId);
    }

    public Asking updateAsking(UUID askingId, UpdateAskingRequest request) {
        return askingService.updateAsking(askingId, request);
    }
}
