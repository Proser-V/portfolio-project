package com.atelierlocal.service;

import org.springframework.stereotype.Service;

import com.atelierlocal.repository.UploadedFileRepo;
import com.atelierlocal.model.UploadedFile;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.User;

import java.util.UUID;

@Service
public class UploadedFileService {

    private final PasswordService passwordService;
    private final UploadedFileRepo uploadedFileRepo;

    public UploadedFileService(PasswordService passwordService, UploadedFileRepo uploadedFileRepo) {
        this.passwordService = passwordService;
        this.uploadedFileRepo = uploadedFileRepo;
    }

    public UploadedFile CreateUploadedFile(UUID id, String extension, Artisan creator, User client, String rawKey) {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setExtension(extension);
        uploadedFile.setClient(client);
        uploadedFile.setCreator(creator);

        String hashedKey = passwordService.hashPassword(rawKey);
        uploadedFile.setKey(hashedKey);

        return uploadedFileRepo.save(uploadedFile);
    }
}
