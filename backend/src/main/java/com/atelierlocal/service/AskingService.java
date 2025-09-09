package com.atelierlocal.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atelierlocal.dto.UpdateAskingRequest;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Asking;
import com.atelierlocal.model.AskingStatus;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.EventCategory;
import com.atelierlocal.repository.AskingRepo;

@Service
public class AskingService {
    
    private final AskingRepo askingRepo;

    public AskingService(AskingRepo askingRepo) {
        this.askingRepo = askingRepo;
    }

    public Asking createAsking(
                    String content,
                    EventCategory eventCategory,
                    Client client,
                    List<ArtisanCategory> artisanCategoryList
                    ) {
        if (content.isBlank() || content == null) {
            throw new IllegalArgumentException("La demande doit contenir une description.");
        }
        if (artisanCategoryList == null || artisanCategoryList.isEmpty()) {
            throw new IllegalArgumentException("Veuillez sélectionner a minima une cétogorie d'artisan.");
        }
        
        Asking asking = new Asking();
        asking.setClient(client);
        asking.setContent(content);
        asking.setStatus(AskingStatus.PENDING);
        if (eventCategory != null) {
            asking.setEventCategory(eventCategory);
        }
        asking.setArtisanCategoryList(artisanCategoryList);

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

    public Asking updateAsking(UUID askingId, UpdateAskingRequest request) {
        Asking asking = askingRepo.findById(askingId)
            .orElseThrow(() -> new RuntimeException("Demande non trouvée."));
        
        if (request.getContent() != null) { asking.setContent(request.getContent());}
        if (request.getArtisanCategoryList() != asking.getArtisanCategoryList()) {
            asking.setArtisanCategoryList(request.getArtisanCategoryList());
        }

        return askingRepo.save(asking);
    }

    public Asking getAskingById(UUID askingId) {
        return askingRepo.findById(askingId)
            .orElseThrow(() -> new RuntimeException("Demande non trouvée."));
    }
}
