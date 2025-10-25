package com.atelierlocal.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atelierlocal.model.Client;

/**
 * Repository pour l'entité Client.
 * 
 * Ce repository fournit des méthodes pour accéder et manipuler les clients.
 * Il hérite de JpaRepository, ce qui lui permet d'utiliser toutes les méthodes CRUD standards :
 *   - save(), saveAll(): persister ou mettre à jour des entités
 *   - findById(), findAll(), findAllById(): récupérer des entités
 *   - existsById(): vérifier l'existence d'une entité
 *   - delete(), deleteById(), deleteAll(): supprimer des entités
 *   - count(): compter le nombre total d'enregistrements
 * 
 * Méthodes personnalisées définies dans ce repository :
 *   - findByEmail(String email) : récupère un client par son email unique
 * 
 * Bonnes pratiques :
 *   - Utiliser findByEmail pour authentification ou vérification d'existence
 *   - Ce repository est utile pour filtrer ou rechercher rapidement un client en fonction de son email
 */
@Repository
public interface ClientRepo extends JpaRepository<Client, UUID> {
    Optional<Client> findByEmail(String email);
}
