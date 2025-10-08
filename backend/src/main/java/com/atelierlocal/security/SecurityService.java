package com.atelierlocal.security;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.User;
import com.atelierlocal.model.UserRole;

@Service
public class SecurityService {

    // ---------- Vérifie la propriété d'un utilisateur (ou admin) ----------

    public void checkUserOwnershipOrAdmin(User currentUser, UUID ownerId) throws AccessDeniedException {
        boolean isAdmin = currentUser.getUserRole() == UserRole.ADMIN;
        boolean isOwner = ownerId.equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Accès refusé : vous n'êtes ni propriétaire, ni administrateur.");
        }
    }

    public void checkClientOwnershipOrAdmin(Client currentClient, UUID ownerId) throws AccessDeniedException {
        boolean isAdmin = currentClient.getUserRole() == UserRole.ADMIN;
        boolean isOwner = ownerId.equals(currentClient.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Accès refusé : vous n'êtes ni propriétaire, ni administrateur.");
        }
    }

    public void checkArtisanOwnershipOrAdmin(Artisan currentArtisan, UUID ownerId) throws AccessDeniedException {
        boolean isAdmin = currentArtisan.getUserRole() == UserRole.ADMIN;
        boolean isOwner = ownerId.equals(currentArtisan.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Accès refusé : vous n'êtes ni propriétaire, ni administrateur.");
        }

    }

    // ---------- Vérifie le role de l'utilisateur ----------

    public void checkAdminOnly(User currentUser) throws AccessDeniedException {
        boolean isAdmin = currentUser.getUserRole() == UserRole.ADMIN;
        if (!isAdmin) {
            throw new AccessDeniedException("Accès refusé : vous n'êtes pas administrateur.");
        }
    }

    public void checkClientOnly(User currentUser) throws AccessDeniedException {
        if (currentUser.getUserRole() != UserRole.CLIENT) {
            throw new AccessDeniedException("Seuls les clients peuvent accéder à cette ressource.");
        }
    }

    public void checkArtisanOnly(User currentUser) throws AccessDeniedException {
        if (currentUser.getUserRole() != UserRole.ARTISAN) {
            throw new AccessDeniedException("Seuls les artisans peuvent accéder à cette ressource.");
        }
    }

    // ---------- Vérifie le role de l'utilisateur ou si il est admin ----------

    public void checkClientOrAdmin(User currentUser) throws AccessDeniedException {
        if (currentUser.getUserRole() != UserRole.CLIENT && currentUser.getUserRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Seuls les clients peuvent accéder à cette ressource.");
        }
    }

    public void checkArtisanOrAdmin(User currentUser) throws AccessDeniedException {
        if (currentUser.getUserRole() != UserRole.ARTISAN && currentUser.getUserRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Seuls les artisans peuvent accéder à cette ressource.");
        }
    }
}
