package com.atelierlocal.repository;

import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.EventCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour l'entité ArtisanCategory.
 * 
 * Ce repository fournit des méthodes pour accéder et manipuler les catégories d'artisans.
 * Il hérite de JpaRepository, ce qui lui permet d'utiliser toutes les méthodes CRUD standards :
 *   - save(), saveAll(): persister ou mettre à jour des entités
 *   - findById(), findAll(), findAllById(): récupérer des entités
 *   - existsById(): vérifier l'existence d'une entité
 *   - delete(), deleteById(), deleteAll(): supprimer des entités
 *   - count(): compter le nombre total d'enregistrements
 * 
 * Méthodes personnalisées définies dans ce repository :
 *   - findByNameIgnoreCase(String name) : récupère une catégorie par son nom, insensible à la casse et aux espaces superflus
 *   - findByEventCategories(EventCategory eventCategory) : récupère toutes les catégories d'artisans associées à une catégorie d'événement donnée
 * 
 * Bonnes pratiques :
 *   - Utiliser findByNameIgnoreCase pour éviter les doublons lors de l'ajout de nouvelles catégories
 *   - findByEventCategories permet de filtrer les catégories liées à un type d'événement spécifique
 */

@Repository
public interface ArtisanCategoryRepo extends JpaRepository<ArtisanCategory, UUID> {
    @Query("SELECT c FROM ArtisanCategory c WHERE LOWER(TRIM(c.name)) = LOWER(TRIM(:name))")
    Optional<ArtisanCategory> findByNameIgnoreCase(@Param("name") String name);

    List<ArtisanCategory> findByEventCategories(EventCategory eventCategory);
}
