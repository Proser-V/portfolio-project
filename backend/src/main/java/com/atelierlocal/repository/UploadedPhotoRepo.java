package com.atelierlocal.repository;

import com.atelierlocal.model.UploadedPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 * Repository pour l'entité UploadedPhoto.
 * 
 * Ce repository gère les opérations de persistance liées aux photos téléchargées 
 * par les artisans (ex. : images de réalisations, de portfolio, etc.).
 * Il hérite de JpaRepository, fournissant toutes les méthodes CRUD standard :
 *   - save(), saveAll(): persister ou mettre à jour des photos
 *   - findById(), findAll(), findAllById(): récupérer des photos
 *   - existsById(): vérifier l’existence d’une photo
 *   - delete(), deleteById(), deleteAll(): supprimer des photos
 *   - count(): compter le nombre total de photos enregistrées
 * 
 * Utilisation typique :
 *   - Enregistrer l’URL d’une photo stockée sur un service externe (ex. AWS S3)
 *   - Associer une photo à un artisan spécifique dans le cadre de son profil ou portfolio
 * 
 * Bonnes pratiques :
 *   - Veiller à la cohérence entre la base de données et le stockage externe (ex. suppression côté S3)
 *   - Gérer correctement les relations avec l’entité Artisan pour éviter les orphelins
 */

@Repository
public interface UploadedPhotoRepo extends JpaRepository<UploadedPhoto, UUID> {}
