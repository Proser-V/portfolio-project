package com.atelierlocal.repository;

import com.atelierlocal.model.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;


/**
 * Repository pour l'entité Avatar.
 * 
 * Ce repository fournit des méthodes pour accéder et manipuler les avatars des utilisateurs.
 * Il hérite de JpaRepository, ce qui lui permet d'utiliser toutes les méthodes CRUD standards :
 *   - save(), saveAll(): persister ou mettre à jour des entités
 *   - findById(), findAll(), findAllById(): récupérer des entités
 *   - existsById(): vérifier l'existence d'une entité
 *   - delete(), deleteById(), deleteAll(): supprimer des entités
 *   - count(): compter le nombre total d'enregistrements
 * 
 * Ce repository ne définit pas de méthodes personnalisées pour l'instant.
 * 
 * Bonnes pratiques :
 *   - Utiliser ce repository pour gérer les avatars afin d'assurer la cohérence avec les utilisateurs
 *   - Toute suppression ou modification doit tenir compte de la relation OneToOne avec l'entité User
 */

@Repository
public interface AvatarRepo extends JpaRepository<Avatar, UUID> {}
