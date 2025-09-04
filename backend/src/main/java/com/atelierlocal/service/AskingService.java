package com.atelierlocal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Asking;
import com.atelierlocal.model.AskingStatus;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.EventCategory;
import com.atelierlocal.repository.AskingRepo;

@Service
public class AskingService {
    
    private final AskingRepo askingRepo;

    public AskingService(AskingRepo askingRepo, AskingStatus askingStatus) {
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
}
