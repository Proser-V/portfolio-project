package com.atelierlocal.service;

import org.springframework.stereotype.Service;

import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Address;

@Service
public class ArtisanService {

    private final PasswordService passwordService;
    private final ArtisanRepo artisanRepo;
    
    public ArtisanService(PasswordService passwordService, ArtisanRepo artisanRepo) {
        this.passwordService = passwordService;
        this.artisanRepo = artisanRepo;
    }

    public Artisan createArtisan(String name, String email, String rawPassword, Address address, String avatar) {
        Artisan artisan = new Artisan();
        artisan.setName(name);
        artisan.setEmail(email);
        artisan.setAddress(address);
        artisan.setAvatar(avatar);

        String hashed = passwordService.hashPassword(rawPassword);
        artisan.setHashedPassword(hashed);

        return artisanRepo.save(artisan);
    }
}
