package com.atelierlocal.service;

import org.springframework.stereotype.Service;

import com.atelierlocal.repository.ClientRepo;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.Address;
import com.atelierlocal.model.Avatar;
import com.atelierlocal.model.UserRole;

@Service
public class ClientService {

    private final PasswordService passwordService;
    private final ClientRepo clientRepo;
    
    public ClientService(PasswordService passwordService, ClientRepo clientRepo) {
        this.passwordService = passwordService;
        this.clientRepo = clientRepo;
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
}
