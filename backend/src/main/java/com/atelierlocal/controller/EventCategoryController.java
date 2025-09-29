package com.atelierlocal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atelierlocal.dto.ArtisanCategoryResponseDTO;
import com.atelierlocal.dto.EventCategoryRequestDTO;
import com.atelierlocal.dto.EventCategoryResponseDTO;
import com.atelierlocal.service.EventCategoryService;


@RestController
@RequestMapping("/api/event-categories")
public class EventCategoryController {
    private final EventCategoryService eventCategoryService;

    public EventCategoryController(EventCategoryService eventCategoryService) {
        this.eventCategoryService = eventCategoryService;
    }

    @PostMapping("/creation")
    public ResponseEntity<EventCategoryResponseDTO> createEventCategory(@RequestBody EventCategoryRequestDTO request) {
        EventCategoryResponseDTO newEventCategory = eventCategoryService.createEventCategory(request);
        return ResponseEntity.ok(newEventCategory);
    }
    
    @GetMapping("/")
    public ResponseEntity<List<EventCategoryResponseDTO>> getAllEventCateogries() {
        List<EventCategoryResponseDTO> allEventCategories = eventCategoryService.getAllEventCategories();
        return ResponseEntity.ok(allEventCategories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventCategoryResponseDTO> getEventCategoryById(@PathVariable UUID id) {
        EventCategoryResponseDTO eventCategory = eventCategoryService.getEventCategoryById(id);
        return ResponseEntity.ok(eventCategory);
    }
    
    @GetMapping("{id}/artisan-categories")
    public ResponseEntity<List<ArtisanCategoryResponseDTO>> getArtisanCategoriesByEvent(@PathVariable UUID id) {
        List<ArtisanCategoryResponseDTO> artisanCategoriesByEvent = eventCategoryService.getArtisanCategoriesByEvent(id);
        return ResponseEntity.ok(artisanCategoriesByEvent);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<EventCategoryResponseDTO> updateEventCategory(@PathVariable UUID id, @RequestBody EventCategoryRequestDTO request) {
        EventCategoryResponseDTO updatedEventCategory = eventCategoryService.updateEventCategory(id, request);
        return ResponseEntity.ok(updatedEventCategory);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteEventCategory(@PathVariable UUID id) {
        eventCategoryService.deleteEventCategory(id);
        return ResponseEntity.noContent().build();
    }
}
