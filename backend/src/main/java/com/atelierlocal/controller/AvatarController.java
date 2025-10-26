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

/**
 * Contrôleur REST pour la gestion des avatars des utilisateurs.
 * Permet le téléchargement et la liaison d'un avatar à un utilisateur.
 */
@RestController
@RequestMapping("/api/avatar")
public class AvatarController {

    private final AvatarService avatarService;
    private final UserRepo userRepo;

    /**
     * Constructeur avec injection des services nécessaires.
     * 
     * @param avatarService service de gestion des avatars
     * @param userRepo repository des utilisateurs
     */
    public AvatarController(AvatarService avatarService, UserRepo userRepo) {
        this.avatarService = avatarService;
        this.userRepo = userRepo;
    }

    // --------------------
    // UPLOAD D'AVATAR
    // --------------------

    /**
     * Upload d'un avatar pour un utilisateur donné.
     * 
     * @param file fichier image à uploader
     * @param userId UUID de l'utilisateur à qui associer l'avatar
     * @return ResponseEntity contenant l'URL de l'avatar ou un message d'erreur
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadAvatar(
        @RequestParam MultipartFile file,
        @RequestParam UUID userId
    ) {
        try {
            // Upload du fichier via le service Avatar
            String url = avatarService.uploadAvatar(file, userId);

            // Récupération de l'utilisateur correspondant
            User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé."));

            // Création ou mise à jour de l'entité Avatar liée à l'utilisateur
            Avatar avatar = user.getAvatar();
            if (avatar == null) {
                avatar = new Avatar();
                avatar.setUser(user);
                user.setAvatar(avatar);
            }
            avatar.setAvatarUrl(url);

            // Sauvegarde de l'utilisateur avec le nouvel avatar
            userRepo.save(user);

            // Retourne l'URL de l'avatar en réponse
            return ResponseEntity.ok(Map.of("url", url));

        } catch (IllegalArgumentException e) {
            // Gestion des erreurs liées à l'utilisateur ou au fichier
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Gestion des erreurs internes
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur lors de l'upload de l'image"));
        }
    }
}
