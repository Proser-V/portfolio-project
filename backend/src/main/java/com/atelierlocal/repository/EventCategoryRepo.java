package com.atelierlocal.repository;

import com.atelierlocal.model.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 * Repository pour l'entité EventCategory.
 * 
 * Ce repository fournit des méthodes pour accéder et manipuler les catégories d'événements.
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
 *   - Utiliser ce repository pour gérer les catégories d'événements afin d'assurer la cohérence des relations avec les demandes et les catégories d'artisans
 *   - Les modifications ou suppressions doivent être effectuées en tenant compte des relations ManyToMany ou OneToMany associées
 */

@Repository
public interface EventCategoryRepo extends JpaRepository<EventCategory, UUID> {}
