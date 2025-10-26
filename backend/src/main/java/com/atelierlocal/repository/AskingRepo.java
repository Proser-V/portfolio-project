package com.atelierlocal.repository;

import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Asking;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.EventCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Repository pour l'entité Asking.
 * 
 * Ce repository fournit des méthodes pour accéder et manipuler les demandes des clients.
 * Il hérite de JpaRepository, ce qui lui permet d'utiliser toutes les méthodes CRUD standards :
 *   - save(), saveAll(): persister ou mettre à jour des entités
 *   - findById(), findAll(), findAllById(): récupérer des entités
 *   - existsById(): vérifier l'existence d'une entité
 *   - delete(), deleteById(), deleteAll(): supprimer des entités
 *   - count(): compter le nombre total d'enregistrements
 * 
 * Méthodes personnalisées définies dans ce repository :
 *   - findAllByArtisanCategory(ArtisanCategory artisanCategory) : récupère toutes les demandes associées à une catégorie d'artisan spécifique
 *   - findAllByArtisanCategoryIn(Collection<ArtisanCategory> categories) : récupère toutes les demandes associées à plusieurs catégories d'artisans
 *   - findAllByEventCategory(EventCategory eventCategory) : récupère toutes les demandes liées à une catégorie d'événement spécifique
 *   - findAllByClient(Client client) : récupère toutes les demandes effectuées par un client donné
 * 
 * Bonnes pratiques :
 *   - Utiliser les méthodes avec filtrage par catégorie ou client pour éviter de charger toutes les demandes inutilement
 *   - findAllByArtisanCategoryIn permet de gérer efficacement des filtres multiples dans les interfaces de recherche
 */

@Repository
public interface AskingRepo extends JpaRepository<Asking, UUID> {
    List<Asking> findAllByArtisanCategory(ArtisanCategory artisanCategory);
    List<Asking> findAllByArtisanCategoryIn(Collection<ArtisanCategory> categories);
    List<Asking> findAllByEventCategory(EventCategory eventCategory);
    List<Asking> findAllByClient(Client client);
}
