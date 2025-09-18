package com.atelierlocal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Asking;
import com.atelierlocal.model.EventCategory;
import com.atelierlocal.repository.ArtisanCategoryRepo;

@Service
public class ArtisanCategoryService {
    
    private final ArtisanCategoryRepo artisanCategoryRepo;

    public ArtisanCategoryService(ArtisanCategoryRepo artisanCategoryRepo) {
        this.artisanCategoryRepo = artisanCategoryRepo;
    }

    public ArtisanCategory createArtisanCategory(
                                String name,
                                String description,
                                List<EventCategory> eventCategories
                                ) {
        if (name.isBlank() || name == null) {
            throw new IllegalArgumentException("Le nom doit être renseigné.");
        }
        if (description.isBlank() || description == null) {
            throw new IllegalArgumentException("La description doit être renseignée.");
        }

        ArtisanCategory artisanCategory = new ArtisanCategory();
        artisanCategory.setName(name);
        artisanCategory.setDescription(description);
        if (eventCategories != null) {
            for (EventCategory eventCategory : eventCategories) {
                eventCategory.getArtisanCategoryList().add(artisanCategory);
            }
            artisanCategory.setEventCategories(eventCategories);
        }

        return artisanCategoryRepo.save(artisanCategory);
    }
}
