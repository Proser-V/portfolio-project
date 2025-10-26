package com.atelierlocal.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.atelierlocal.dto.ClientRequestDTO;
import com.atelierlocal.dto.ClientResponseDTO;
import com.atelierlocal.model.Avatar;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.UserRole;
import com.atelierlocal.repository.AvatarRepo;
import com.atelierlocal.repository.ClientRepo;
import com.atelierlocal.security.SecurityService;

import jakarta.persistence.EntityNotFoundException;

/**
 * Service pour la gestion des clients et administrateurs.
 * 
 * Fournit des méthodes pour :
 * - création, mise à jour et suppression de clients/admins,
 * - récupération de clients par ID ou email,
 * - gestion des avatars,
 * - activation/désactivation (ban/unban) de comptes.
 */
@Service
public class ClientService {

    private final PasswordService passwordService;
    private final ClientRepo clientRepo;
    private final AvatarService avatarService;
    private final AvatarRepo avatarRepo;
    private final SecurityService securityService;

    public ClientService(
                PasswordService passwordService,
                ClientRepo clientRepo,
                AvatarService avatarService,
                AvatarRepo avatarRepo,
                SecurityService securityService
                ) {
        this.passwordService = passwordService;
        this.clientRepo = clientRepo;
        this.avatarService = avatarService;
        this.avatarRepo = avatarRepo;
        this.securityService = securityService;
    }

    /**
     * Crée un nouveau client avec les informations fournies.
     * 
     * @param dto données du client à créer
     * @return DTO du client créé
     * @throws IllegalArgumentException si certaines données sont invalides ou manquantes
     */
    public ClientResponseDTO createClient(ClientRequestDTO dto) {
        // Vérification email unique
        if (clientRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà enregistré.");
        }
        // Vérification des champs obligatoires
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Veuillez saisir un mot de passe.");
        }
        if (dto.getFirstName() == null || dto.getFirstName().isBlank()) {
            throw new IllegalArgumentException("Veuillez entrer votre prénom.");
        }
        if (dto.getLastName() == null || dto.getLastName().isBlank()) {
            throw new IllegalArgumentException("Veuillez entrer votre nom.");
        }

        Client client = new Client();
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setEmail(dto.getEmail());
        
        if (dto.getLatitude() == null) {
            throw new IllegalArgumentException("La latitude ne peut être vide.");
        }
        if (dto.getLongitude() == null) {
            throw new IllegalArgumentException("La longitude ne peut être vide.");
        }
        client.setLatitude(dto.getLatitude());
        client.setLongitude(dto.getLongitude());

        // Gestion de l'avatar : upload si fourni, sinon placeholder par défaut
        Avatar avatar = null;
        if (dto.getAvatar() != null) {
            String avatarUrl = avatarService.uploadAvatar(dto.getAvatar(), null);
            avatar = new Avatar();
            avatar.setAvatarUrl(avatarUrl);
            avatar.setExtension(avatarService.getFileExtension(dto.getAvatar()));
            avatar.setUser(client);
        } else {
            avatar = new Avatar();
            avatar.setAvatarUrl("https://d1gmao6ee1284v.cloudfront.net/avatar-placeholder.png");
            avatar.setExtension("png");
            avatar.setUser(client);
        }
        client.setAvatar(avatar);

        // Hashage du mot de passe
        String hashed = passwordService.hashPassword(dto.getPassword());
        client.setHashedPassword(hashed);

        client.setUserRole(UserRole.CLIENT);
        client.setActive(true);
        client.setPhoneNumber(dto.getPhoneNumber());

        Client savedClient = clientRepo.save(client);
        return new ClientResponseDTO(savedClient);
    }

    /**
     * Crée un administrateur ou un client selon le rôle indiqué dans le DTO.
     * 
     * @param dto données du client/admin à créer
     * @return DTO du client/admin créé
     */
    public ClientResponseDTO createAdmin(ClientRequestDTO dto) {
        // Vérification similaire à createClient
        if (clientRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà enregistré.");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Veuillez saisir un mot de passe.");
        }
        if (dto.getFirstName() == null || dto.getFirstName().isBlank()) {
            throw new IllegalArgumentException("Veuillez entrer votre prénom.");
        }
        if (dto.getLastName() == null || dto.getLastName().isBlank()) {
            throw new IllegalArgumentException("Veuillez entrer votre nom.");
        }

        Client client = new Client();
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setEmail(dto.getEmail());
        
        if (dto.getLatitude() == null) {
            throw new IllegalArgumentException("La latitude ne peut être vide.");
        }
        if (dto.getLongitude() == null) {
            throw new IllegalArgumentException("La longitude ne peut être vide.");
        }
        client.setLatitude(dto.getLatitude());
        client.setLongitude(dto.getLongitude());

        // Gestion de l'avatar
        Avatar avatar = null;
        if (dto.getAvatar() != null) {
            String avatarUrl = avatarService.uploadAvatar(dto.getAvatar(), null);
            avatar = new Avatar();
            avatar.setAvatarUrl(avatarUrl);
            avatar.setExtension(avatarService.getFileExtension(dto.getAvatar()));
            avatar.setUser(client);
        } else {
            avatar = new Avatar();
            avatar.setAvatarUrl("https://d1gmao6ee1284v.cloudfront.net/avatar-placeholder.png");
            avatar.setExtension("png");
            avatar.setUser(client);
        }
        client.setAvatar(avatar);

        // Hashage du mot de passe
        String hashed = passwordService.hashPassword(dto.getPassword());
        client.setHashedPassword(hashed);

        // Attribution du rôle : CLIENT par défaut, sinon le rôle fourni
        client.setUserRole(dto.getRole() != null ? dto.getRole() : UserRole.CLIENT);
        client.setActive(true);
        client.setPhoneNumber(dto.getPhoneNumber());

        Client savedClient = clientRepo.save(client);
        return new ClientResponseDTO(savedClient);
    }

    /**
     * Supprime un client (admin only).
     * 
     * @param cientId ID du client à supprimer
     * @param currentClient utilisateur courant pour vérification admin
     */
    public void deleteClient(UUID cientId, Client currentClient ) {
        securityService.checkAdminOnly(currentClient);
        Client client = clientRepo.findById(cientId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));

        clientRepo.delete(client);
    }

    /**
     * Met à jour un client existant.
     * 
     * @param clientId ID du client à mettre à jour
     * @param request DTO contenant les données à mettre à jour
     * @param currentClient utilisateur courant pour vérification
     * @return DTO du client mis à jour
     */
    public ClientResponseDTO updateClient(UUID clientId, ClientRequestDTO request, Client currentClient) {
        securityService.checkClientOrAdmin(currentClient);
        Client client = clientRepo.findById(clientId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));
        
        // Mise à jour des informations personnelles
        if (request.getFirstName() != null) { client.setFirstName(request.getFirstName()); }
        if (request.getLastName() != null) { client.setLastName(request.getLastName()); }

        // Mise à jour de la localisation
        if (request.getLatitude() != null) { client.setLatitude(request.getLatitude()); }
        if (request.getLongitude() != null) { client.setLongitude(request.getLongitude()); }

        // Mise à jour du contact
        if (request.getEmail() != null) { client.setEmail(request.getEmail()); }
        if (request.getPhoneNumber() != null) { client.setPhoneNumber(request.getPhoneNumber()); }

        // Mise à jour du mot de passe
        if (request.getPassword() != null) {
            String hashed = passwordService.hashPassword(request.getPassword());
            client.setHashedPassword(hashed);
        }

        // Mise à jour de l'avatar
        if (request.getAvatar() != null) {
            String avatarUrl = avatarService.uploadAvatar(request.getAvatar(), clientId);
            Avatar avatar = client.getAvatar();
            if (avatar == null) {
                avatar = new Avatar();
                avatar.setUser(client);
            }
            avatar.setAvatarUrl(avatarUrl);
            avatar.setExtension(avatarService.getFileExtension(request.getAvatar()));
            avatarRepo.save(avatar);
        }

        Client updatedClient = clientRepo.save(client);
        return new ClientResponseDTO(updatedClient);
    }

    /**
     * Récupère un client par son ID.
     * 
     * @param clientId ID du client
     * @return DTO du client
     */
    public ClientResponseDTO getClientById(UUID clientId) {
        Client client = clientRepo.findById(clientId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));
        return new ClientResponseDTO(client);
    }

    /**
     * Récupère un client par email (admin only).
     * 
     * @param email email du client
     * @param currentClient utilisateur courant
     * @return DTO du client
     */
    public ClientResponseDTO getClientByEmail(String email, Client currentClient) {
        securityService.checkAdminOnly(currentClient);
        Client client = clientRepo.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));
        return new ClientResponseDTO(client);
    }

    /**
     * Récupère tous les clients (admin only).
     * 
     * @param currentClient utilisateur courant
     * @return liste de DTO clients
     */
    public List<ClientResponseDTO> getAllClients(Client currentClient) {
        securityService.checkAdminOnly(currentClient);
        return clientRepo.findAll().stream()
                                .map(ClientResponseDTO::new)
                                .collect(Collectors.toList());
    }

    /**
     * Active ou désactive un client (ban/unban).
     * 
     * @param clientId ID du client
     * @param currentClient utilisateur courant (admin)
     * @return DTO du client après modification de l'état
     */
    public ClientResponseDTO banClient(UUID clientId, Client currentClient) {
        securityService.checkAdminOnly(currentClient);
        Client client = clientRepo.findById(clientId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));

        // Basculer l'état actif/inactif
        client.setActive(!client.getActive());

        clientRepo.save(client);
        return new ClientResponseDTO(client);
    }
}
