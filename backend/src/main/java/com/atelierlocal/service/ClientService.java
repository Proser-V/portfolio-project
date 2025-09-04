package com.atelierlocal.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atelierlocal.repository.AvatarRepo;
import com.atelierlocal.repository.ClientRepo;
import com.atelierlocal.model.Client;
import com.atelierlocal.dto.UpdateClientRequest;
import com.atelierlocal.model.Address;
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

    public Client createClient(
                    String firstName,
                    String lastName,
                    String email,
                    Address address,
                    String rawPassword,
                    Avatar avatar,
                    Boolean isActive,
                    UserRole userRole
                    ) {
        if (clientRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        Client client = new Client();
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setEmail(email);
        client.setAddress(address);
        client.setAvatar(avatar);

        String hashed = passwordService.hashPassword(rawPassword);
        client.setHashedPassword(hashed);
        client.setUserRole(userRole != null ? userRole : UserRole.CLIENT); // Possibilité de créer un admin en passant le bon role
        client.setActive(true);

        return clientRepo.save(client);
    }

    public void deleteClient(UUID cientId) {
        Client client = clientRepo.findById(cientId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé."));

        clientRepo.delete(client);
    }

    public Client updateClient(UUID clientId, UpdateClientRequest request) {
        Client client = clientRepo.findById(clientId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé."));
        
        if (request.getFirstName() != null) { client.setFirstName(request.getFirstName()); }
        if (request.getLastName() != null) { client.setLastName(request.getLastName()); }
        if (request.getAddress() != null) { client.setAddress(request.getAddress()); }
        if (request.getEmail() != null) { client.setEmail(request.getEmail()); }
        if (request.getRawPassword() != null) {
            String hashed = passwordService.hashPassword(request.getRawPassword());
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
            avatar.setUrl(avatarUrl);
            avatar.setExtension(avatarService.getFileExtension(request.getAvatar()));

            avatarRepo.save(avatar);
        }

        return clientRepo.save(client);
    }

    public Client getClientById(UUID clientId) {
        Client client = clientRepo.findById(clientId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé."));
        return client;
    }

    public Client getClientByEmail(String email) {
        Client client = clientRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé."));
        return client;
    }

    public List<Client> getAllClients() {
        List<Client> clientList = clientRepo.findAll();
        return clientList;
    }
}
