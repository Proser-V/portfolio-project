package com.atelierlocal.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atelierlocal.model.Recommendation;

/**
 * Repository pour l'entité Recommendation.
 * 
 * Ce repository gère les opérations de persistance liées aux recommandations
 * entre clients et artisans. Il hérite de JpaRepository, offrant toutes les 
 * méthodes CRUD standard :
 *   - save(), saveAll(): persister ou mettre à jour des recommandations
 *   - findById(), findAll(), findAllById(): récupérer des recommandations
 *   - existsById(): vérifier l’existence d’une recommandation
 *   - delete(), deleteById(), deleteAll(): supprimer des recommandations
 *   - count(): compter le nombre total de recommandations
 * 
 * Utilisation typique :
 *   - Enregistrer une nouvelle recommandation lorsqu’un client recommande un artisan.
 *   - Consulter le nombre total de recommandations reçues par un artisan pour 
 *     établir un score de confiance ou de réputation.
 * 
 * Bonnes pratiques :
 *   - Les recommandations étant liées à des entités Client et Artisan, 
 *     vérifier la cohérence des relations avant de les persister.
 *   - Éviter la duplication des recommandations (ex : un client ne devrait pas
 *     pouvoir recommander plusieurs fois le même artisan).
 */
@Repository
public interface RecommendationRepo extends JpaRepository<Recommendation, UUID> {}
