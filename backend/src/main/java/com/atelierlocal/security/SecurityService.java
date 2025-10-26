package com.atelierlocal.security;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.User;
import com.atelierlocal.model.UserRole;

/**
 * Service de sécurité pour vérifier les autorisations et la propriété des ressources.
 * 
 * Fournit des méthodes pour :
 * - vérifier si un utilisateur est propriétaire d'une ressource ou administrateur,
 * - vérifier les rôles utilisateurs pour contrôler l'accès aux ressources.
 */
@Service
public class SecurityService {

    // ---------- Vérification de la propriété ou rôle admin ----------

    /**
     * Vérifie si l'utilisateur courant est soit administrateur, soit propriétaire d'une ressource.
     * 
     * @param currentUser utilisateur courant
     * @param ownerId ID du propriétaire attendu
     * @throws AccessDeniedException si l'utilisateur n'est ni admin ni propriétaire
     */
    public void checkUserOwnershipOrAdmin(User currentUser, UUID ownerId) throws AccessDeniedException {
        boolean isAdmin = currentUser.getUserRole() == UserRole.ADMIN;
        boolean isOwner = ownerId.equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Accès refusé : vous n'êtes ni propriétaire, ni administrateur.");
        }
    }

    /**
     * Vérifie si le client courant est soit administrateur, soit propriétaire.
     */
    public void checkClientOwnershipOrAdmin(Client currentClient, UUID ownerId) throws AccessDeniedException {
        boolean isAdmin = currentClient.getUserRole() == UserRole.ADMIN;
        boolean isOwner = ownerId.equals(currentClient.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Accès refusé : vous n'êtes ni propriétaire, ni administrateur.");
        }
    }

    /**
     * Vérifie si l'artisan courant est soit administrateur, soit propriétaire.
     */
    public void checkArtisanOwnershipOrAdmin(Artisan currentArtisan, UUID ownerId) throws AccessDeniedException {
        boolean isAdmin = currentArtisan.getUserRole() == UserRole.ADMIN;
        boolean isOwner = ownerId.equals(currentArtisan.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Accès refusé : vous n'êtes ni propriétaire, ni administrateur.");
        }
    }

    // ---------- Vérification stricte du rôle ----------

    /**
     * Vérifie que l'utilisateur courant est administrateur.
     */
    public void checkAdminOnly(User currentUser) throws AccessDeniedException {
        boolean isAdmin = currentUser.getUserRole() == UserRole.ADMIN;
        if (!isAdmin) {
            throw new AccessDeniedException("Accès refusé : vous n'êtes pas administrateur.");
        }
    }

    /**
     * Vérifie que l'utilisateur courant est un client.
     */
    public void checkClientOnly(User currentUser) throws AccessDeniedException {
        if (currentUser.getUserRole() != UserRole.CLIENT) {
            throw new AccessDeniedException("Seuls les clients peuvent accéder à cette ressource.");
        }
    }

    /**
     * Vérifie que l'utilisateur courant est un artisan.
     */
    public void checkArtisanOnly(User currentUser) throws AccessDeniedException {
        if (currentUser.getUserRole() != UserRole.ARTISAN) {
            throw new AccessDeniedException("Seuls les artisans peuvent accéder à cette ressource.");
        }
    }

    // ---------- Vérification du rôle ou rôle admin ----------

    /**
     * Vérifie que l'utilisateur est client ou administrateur.
     */
    public void checkClientOrAdmin(User currentUser) throws AccessDeniedException {
        if (currentUser.getUserRole() != UserRole.CLIENT && currentUser.getUserRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Seuls les clients peuvent accéder à cette ressource.");
        }
    }

    /**
     * Vérifie que l'utilisateur est artisan ou administrateur.
     */
    public void checkArtisanOrAdmin(User currentUser) throws AccessDeniedException {
        if (currentUser.getUserRole() != UserRole.ARTISAN && currentUser.getUserRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Seuls les artisans peuvent accéder à cette ressource.");
        }
    }
}
