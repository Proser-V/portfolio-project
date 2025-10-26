package com.atelierlocal.repository;

import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.ArtisanCategory;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour l'entité Artisan.
 * 
 * Ce repository fournit des méthodes pour accéder et manipuler les artisans.
 * Il hérite de JpaRepository, ce qui lui permet d'utiliser toutes les méthodes CRUD standards :
 *   - save(), saveAll(): persister ou mettre à jour des entités
 *   - findById(), findAll(), findAllById(): récupérer des entités
 *   - existsById(): vérifier l'existence d'une entité
 *   - delete(), deleteById(), deleteAll(): supprimer des entités
 *   - count(): compter le nombre total d'enregistrements
 * 
 * Méthodes personnalisées définies dans ce repository :
 *   - findByEmail(String email) : récupère un artisan par son email unique
 *   - findAllByCategory(ArtisanCategory artisanCategory) : récupère tous les artisans appartenant à une catégorie donnée
 *   - findTop10ByOrderByRecommendationsDesc() : récupère les 10 artisans les mieux recommandés, 
 *     avec un EntityGraph pour charger simultanément l'avatar et la catégorie afin d'optimiser les performances
 * 
 * Bonnes pratiques :
 *   - Utiliser findByEmail pour authentification ou vérification d'existence
 *   - Utiliser findAllByCategory pour filtrer les artisans selon leur spécialité
 *   - findTop10ByOrderByRecommendationsDesc permet de récupérer rapidement les artisans les plus populaires sans requêtes supplémentaires pour leurs relations
 */

@Repository
public interface ArtisanRepo extends JpaRepository<Artisan, UUID> {
    Optional<Artisan> findByEmail(String email);
    List<Artisan> findAllByCategory(ArtisanCategory artisanCategory);
    @EntityGraph(attributePaths = {"avatar", "category"})
    List<Artisan> findTop10ByOrderByRecommendationsDesc();
}
