package com.atelierlocal.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.atelierlocal.model.Avatar;
import com.atelierlocal.model.User;
import com.atelierlocal.repository.UserRepo;
import com.atelierlocal.service.AvatarService;

@RestController
@RequestMapping("/api/avatar")
public class AvatarController {
    private final AvatarService avatarService;
    private final UserRepo userRepo;

    public AvatarController(AvatarService avatarService, UserRepo userRepo) {
        this.avatarService = avatarService;
        this.userRepo = userRepo;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadAvatar(
        @RequestParam MultipartFile file,
        @RequestParam UUID userId
    ) {
        try {
            String url = avatarService.uploadAvatar(file, userId);
            User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé."));
            Avatar avatar = user.getAvatar();
            if (avatar == null) {
                avatar = new Avatar();
                avatar.setUser(user);
                user.setAvatar(avatar);
            }
            avatar.setAvatarUrl(url);
            userRepo.save(user);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur lors de l'upload de l'image"));
        }
    }
}