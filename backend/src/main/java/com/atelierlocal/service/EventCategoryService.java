package com.atelierlocal.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Asking;
import com.atelierlocal.model.EventCategory;
import com.atelierlocal.repository.EventCategoryRepo;

@Service
public class EventCategoryService {
    private final EventCategoryRepo eventCategoryRepo;

    public EventCategoryService(EventCategoryRepo eventCategoryRepo) {
        this.eventCategoryRepo = eventCategoryRepo;
    }

    public EventCategory createEventCategory(String name, List<ArtisanCategory> artisanCategoryList, List<Asking> askingList) {
        EventCategory eventCategory = new EventCategory();
        eventCategory.setName(name);
        eventCategory.setArtisanCategoryList(artisanCategoryList);
        eventCategory.setAskingsList(askingList);
        return eventCategoryRepo.save(eventCategory);
    }

    public 
}
