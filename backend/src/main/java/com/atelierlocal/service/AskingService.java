package com.atelierlocal.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.atelierlocal.dto.AskingRequestDTO;
import com.atelierlocal.dto.AskingResponseDTO;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Asking;
import com.atelierlocal.model.AskingStatus;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.EventCategory;
import com.atelierlocal.model.User;
import com.atelierlocal.repository.AskingRepo;
import com.atelierlocal.repository.ClientRepo;
import com.atelierlocal.repository.EventCategoryRepo;
import com.atelierlocal.security.SecurityService;
import com.atelierlocal.repository.ArtisanCategoryRepo;

@Service
public class AskingService {
    
    private final AskingRepo askingRepo;
    private final ArtisanCategoryRepo artisanCategoryRepo;
    private final ClientRepo clientRepo;
    private final EventCategoryRepo eventCategoryRepo;
    private final SecurityService securityService;

    public AskingService(
                AskingRepo askingRepo,
                ArtisanCategoryRepo artisanCategoryRepo,
                ClientRepo clientRepo,
                EventCategoryRepo eventCategoryRepo,
                SecurityService securityService) {
        this.askingRepo = askingRepo;
        this.artisanCategoryRepo = artisanCategoryRepo;
        this.clientRepo = clientRepo;
        this.eventCategoryRepo = eventCategoryRepo;
        this.securityService = securityService;
    }

    public AskingResponseDTO createAsking(AskingRequestDTO dto, Client currentClient) {
        securityService.checkClientOnly(currentClient);

        if (dto.getTitle().isBlank() || dto.getTitle() == null) {
            throw new IllegalArgumentException("La demande doit contenir un titre.");
        }
        if (dto.getContent().isBlank() || dto.getContent() == null) {
            throw new IllegalArgumentException("La demande doit contenir une description.");
        }
        if (dto.getArtisanCategoryId() == null) {
            throw new IllegalArgumentException("Veuillez sélectionner la cétogorie d'artisan souhaitée.");
        }
        Client client = clientRepo.findById(dto.getClientId())
            .orElseThrow(() -> new IllegalArgumentException("Client introuvable."));
        
        ArtisanCategory artisanCategory = artisanCategoryRepo.findById(dto.getArtisanCategoryId())
            .orElseThrow(() -> new IllegalArgumentException("Catégorie d'artisan introuvable."));

        EventCategory eventCategory = null;
        if (dto.getEventCategoryId() != null) {
            eventCategory = eventCategoryRepo.findById(dto.getEventCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Catégorie d'événement introuvable."));
        }

        Asking asking = new Asking();
        asking.setClient(client);
        asking.setTitle(dto.getTitle());
        asking.setContent(dto.getContent());
        asking.setStatus(AskingStatus.PENDING);
        if (dto.getEventCategoryId() != null) {
            asking.setEventCategory(eventCategory);
        }
        if (dto.getEventDate() != null) {
            asking.setEventDate(dto.getEventDate());
        }
        if (dto.getEventLocalisation() != null) {
            asking.setEventLocalisation(dto.getEventLocalisation());
        }
        asking.setArtisanCategory(artisanCategory);

        Asking newAsking = askingRepo.save(asking);
        return new AskingResponseDTO(newAsking);
    }

    public AskingResponseDTO patchAskingStatus(UUID askingId, AskingStatus newStatus, Client currentClient) {
        Asking asking = askingRepo.findById(askingId)
            .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée."));

        securityService.checkClientOwnershipOrAdmin(currentClient, asking.getClient().getId());

        AskingStatus currentStatus = asking.getStatus();

        if (currentStatus != AskingStatus.PENDING) {
            throw new RuntimeException("Impossible de modifier une demande déjà " + currentStatus.name().toLowerCase() + ".");
        }

        if (newStatus != AskingStatus.DONE && newStatus != AskingStatus.CANCELLED) {
            throw new IllegalArgumentException("Statut invalide : " + newStatus);
        }

        asking.setStatus(newStatus);
        Asking patchedAsking = askingRepo.save(asking);
        return new AskingResponseDTO(patchedAsking);
    }

    public void deleteAsking(UUID askingId, Client currentClient) {
        Asking asking = askingRepo.findById(askingId)
            .orElseThrow(() -> new RuntimeException("Demande non trouvée."));

        securityService.checkClientOwnershipOrAdmin(currentClient, asking.getClient().getId());
    }

    public AskingResponseDTO updateAsking(UUID askingId, AskingRequestDTO request, Client currentClient) {
        Asking asking = askingRepo.findById(askingId)
            .orElseThrow(() -> new RuntimeException("Demande non trouvée."));

        securityService.checkClientOwnershipOrAdmin(currentClient, asking.getClient().getId());

        if (request.getContent() != null) { asking.setContent(request.getContent());}
        if (request.getTitle() != null) { asking.setTitle(request.getTitle());}
        if (request.getArtisanCategoryId() != null &&
        !request.getArtisanCategoryId().equals(asking.getArtisanCategory().getId())) {
            ArtisanCategory artisanCategory = artisanCategoryRepo.findById(request.getArtisanCategoryId())
                .orElseThrow(() -> new RuntimeException("Catégorie d'artisan non trouvée."));

            asking.setArtisanCategory(artisanCategory);
        }

        Asking updatedAsking = askingRepo.save(asking);
        return new AskingResponseDTO(updatedAsking);
    }

    public AskingResponseDTO getAskingById(UUID askingId) {
        Asking asking = askingRepo.findById(askingId)
            .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée."));
        return new AskingResponseDTO(asking);
    }

    public List<AskingResponseDTO> getAskingsByClient(UUID clientId, Client currentClient) {
        Client client = clientRepo.findById(clientId)
            .orElseThrow(() -> new IllegalArgumentException("Client non trouvé."));

        securityService.checkClientOwnershipOrAdmin(currentClient, clientId);

        List<Asking> askings = askingRepo.findAllByClient(client);

        return askings.stream()
            .map(AskingResponseDTO::new)
            .collect(Collectors.toList());
    }

    public List<AskingResponseDTO> getAllAskings(User currentUser) {
        securityService.checkAdminOnly(currentUser);

        List<Asking> allAskings = askingRepo.findAll();

        return allAskings.stream()
            .map(AskingResponseDTO::new)
            .collect(Collectors.toList());
    }

    public List<AskingResponseDTO> getAskingsByCategory(UUID categoryId, User currentUser) {
        securityService.checkArtisanOrAdmin(currentUser);

        ArtisanCategory category = artisanCategoryRepo.findById(categoryId)
                                    .orElseThrow(() -> new IllegalArgumentException("Catégorie d'artisan non trouvée."));

        List<Asking> askingsByCategory = askingRepo.findAllByArtisanCategory(category);
        return askingsByCategory.stream()
                .map(AskingResponseDTO::new)
                .collect(Collectors.toList());
    }
}
