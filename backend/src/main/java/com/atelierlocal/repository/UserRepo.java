package com.atelierlocal.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atelierlocal.model.User;

/**
 * Repository pour l'entité User.
 * 
 * Ce repository gère la persistance et la récupération des utilisateurs 
 * (clients, artisans et administrateurs) au sein de la base de données.
 * Il hérite de JpaRepository, fournissant les principales opérations CRUD :
 *   - save(), saveAll(): créer ou mettre à jour un utilisateur
 *   - findById(), findAll(), findAllById(): récupérer un ou plusieurs utilisateurs
 *   - existsById(): vérifier l’existence d’un utilisateur
 *   - delete(), deleteById(), deleteAll(): supprimer un ou plusieurs utilisateurs
 *   - count(): compter le nombre total d’utilisateurs enregistrés
 * 
 * Méthodes personnalisées :
 *   - findByEmail(String email): recherche un utilisateur par son adresse e-mail,
 *     utilisée notamment lors de l’authentification et de la vérification d’unicité.
 * 
 * Bonnes pratiques :
 *   - Toujours retourner un Optional<User> pour éviter les NullPointerException.
 *   - Indexer la colonne email dans la base de données pour accélérer les recherches.
 *   - Ne jamais exposer les mots de passe (même hashés) dans les réponses API.
 */
@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
