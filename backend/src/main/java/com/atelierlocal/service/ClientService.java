package com.atelierlocal.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.atelierlocal.repository.AvatarRepo;
import com.atelierlocal.repository.ClientRepo;

import jakarta.persistence.EntityNotFoundException;

import com.atelierlocal.model.Client;
import com.atelierlocal.dto.ClientRequestDTO;
import com.atelierlocal.dto.ClientResponseDTO;
import com.atelierlocal.model.Avatar;
import com.atelierlocal.model.UserRole;

@Service
public class ClientService {

    private final PasswordService passwordService;
    private final ClientRepo clientRepo;
    private final AvatarService avatarService;
    private final AvatarRepo avatarRepo;

    public ClientService(
                PasswordService passwordService,
                ClientRepo clientRepo,
                AvatarService avatarService,
                AvatarRepo avatarRepo
                ) {
        this.passwordService = passwordService;
        this.clientRepo = clientRepo;
        this.avatarService = avatarService;
        this.avatarRepo = avatarRepo;
    }

    public ClientResponseDTO createClient(ClientRequestDTO dto) {
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
        Avatar avatar = null;
        if (dto.getAvatar() != null) {
            String avatarUrl = avatarService.uploadAvatar(dto.getAvatar(), null);
            avatar = new Avatar();
            avatar.setAvatarUrl(avatarUrl);
            avatar.setExtension(avatarService.getFileExtension(dto.getAvatar()));
        }
        client.setAvatar(avatar);
        String hashed = passwordService.hashPassword(dto.getPassword());
        client.setHashedPassword(hashed);
        client.setUserRole(dto.getRole() != null ? dto.getRole() : UserRole.CLIENT); // Possibilité de créer un admin en passant le bon role
        client.setActive(true);

        Client savedClient = clientRepo.save(client);
        return new ClientResponseDTO(savedClient);
    }

    public void deleteClient(UUID cientId) {
        Client client = clientRepo.findById(cientId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));

        clientRepo.delete(client);
    }

    public ClientResponseDTO updateClient(UUID clientId, ClientRequestDTO request) {
        Client client = clientRepo.findById(clientId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));
        
        if (request.getFirstName() != null) { client.setFirstName(request.getFirstName()); }
        if (request.getLastName() != null) { client.setLastName(request.getLastName()); }

        if (request.getLatitude() != null) { client.setLatitude(request.getLatitude()); }
        if (request.getLongitude() != null) { client.setLongitude(request.getLongitude()); }

        if (request.getEmail() != null) { client.setEmail(request.getEmail()); }
        if (request.getPhoneNumber() != null) { client.setPhoneNumber(request.getPhoneNumber()); }
        if (request.getPassword() != null) {
            String hashed = passwordService.hashPassword(request.getPassword());
            client.setHashedPassword(hashed);
        }
        if (request.getAvatar() != null) {
            // Upload de l'image sur AWS S3
            String avatarUrl = avatarService.uploadAvatar(request.getAvatar(), clientId);

            // Modification de l'entité Avatar
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

    public ClientResponseDTO getClientById(UUID clientId) {
        Client client = clientRepo.findById(clientId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));
        return new ClientResponseDTO(client);
    }

    public ClientResponseDTO getClientByEmail(String email) {
        Client client = clientRepo.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));
        return new ClientResponseDTO(client);
    }

    public List<ClientResponseDTO> getAllClients() {
        return clientRepo.findAll().stream()
                                .map(ClientResponseDTO::new)
                                .collect(Collectors.toList());
    }

    public void banClient(UUID clientId) {
        Client client = clientRepo.findById(clientId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));
        client.setActive(false);
        clientRepo.save(client);
    }
}
