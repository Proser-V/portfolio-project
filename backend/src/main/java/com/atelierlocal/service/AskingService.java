package com.atelierlocal.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atelierlocal.dto.AskingRequestDTO;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Asking;
import com.atelierlocal.model.AskingStatus;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.EventCategory;
import com.atelierlocal.repository.AskingRepo;
import com.atelierlocal.repository.ArtisanCategoryRepo;

@Service
public class AskingService {
    
    private final AskingRepo askingRepo;
    private final ArtisanCategoryRepo artisanCategoryRepo;

    public AskingService(AskingRepo askingRepo, ArtisanCategoryRepo artisanCategoryRepo) {
        this.askingRepo = askingRepo;
        this.artisanCategoryRepo = artisanCategoryRepo;
    }

    public Asking createAsking(
                    String content,
                    EventCategory eventCategory,
                    Client client,
                    ArtisanCategory artisanCategory
                    ) {
        if (content.isBlank() || content == null) {
            throw new IllegalArgumentException("La demande doit contenir une description.");
        }
        if (artisanCategory == null) {
            throw new IllegalArgumentException("Veuillez sélectionner la cétogorie d'artisan souhaitée.");
        }
        
        Asking asking = new Asking();
        asking.setClient(client);
        asking.setContent(content);
        asking.setStatus(AskingStatus.PENDING);
        if (eventCategory != null) {
            asking.setEventCategory(eventCategory);
        }
        asking.setArtisanCategory(artisanCategory);

        return askingRepo.save(asking);
    }

    public Asking closeAsking(UUID askingId) {
        Asking asking = askingRepo.findById(askingId)
            .orElseThrow(() -> new RuntimeException("Demande non trouvée."));
        
        if (asking.getStatus() == AskingStatus.PENDING) {
            asking.setStatus(AskingStatus.DONE);
        } else if (asking.getStatus() == AskingStatus.DONE) {
            throw new RuntimeException("Demande déjà close.");
        } else {
            throw new RuntimeException("Demande déjà annulée.");
        }

        return askingRepo.save(asking);
    }

    public Asking cancelAsking(UUID askingId) {
        Asking asking = askingRepo.findById(askingId)
            .orElseThrow(() -> new RuntimeException("Demande non trouvée."));
        
        if (asking.getStatus() == AskingStatus.PENDING) {
            asking.setStatus(AskingStatus.CANCELLED);
        } else if (asking.getStatus() == AskingStatus.DONE) {
            throw new RuntimeException("Demande déjà close.");
        } else {
            throw new RuntimeException("Demande déjà annulée.");
        }

        return askingRepo.save(asking);
    }

    public void deleteAsking(UUID askingId) {
        Asking asking = askingRepo.findById(askingId)
            .orElseThrow(() -> new RuntimeException("Demande non trouvée."));

        askingRepo.delete(asking);
    }

    public Asking updateAsking(UUID askingId, AskingRequestDTO request) {
        Asking asking = askingRepo.findById(askingId)
            .orElseThrow(() -> new RuntimeException("Demande non trouvée."));
        
        if (request.getContent() != null) { asking.setContent(request.getContent());}

        if (request.getArtisanCategoryId() != null &&
        !request.getArtisanCategoryId().equals(asking.getArtisanCategory().getId())) {
            ArtisanCategory artisanCategory = artisanCategoryRepo.findById(request.getArtisanCategoryId())
                .orElseThrow(() -> new RuntimeException("Catégorie d'artisan non trouvée."));

            asking.setArtisanCategory(artisanCategory);
        }

        return askingRepo.save(asking);
    }

    public Asking getAskingById(UUID askingId) {
        return askingRepo.findById(askingId)
            .orElseThrow(() -> new RuntimeException("Demande non trouvée."));
    }
}
