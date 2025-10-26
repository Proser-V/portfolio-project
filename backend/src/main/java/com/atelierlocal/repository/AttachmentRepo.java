package com.atelierlocal.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.atelierlocal.model.Attachment;

/**
 * Repository pour l'entité Attachment.
 * 
 * Ce repository fournit des méthodes pour accéder et manipuler les fichiers attachés aux messages.
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
 *   - Utiliser ce repository pour gérer les attachments liés aux messages afin de maintenir l'intégrité des relations
 *   - Les suppressions en cascade doivent être gérées via les relations définies dans l'entité Message
 */

@Repository
public interface AttachmentRepo extends JpaRepository<Attachment, UUID> {
    @Modifying
    @Query("DELETE FROM Attachment a WHERE a.message.id IN " +
           "(SELECT m.id FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId)")
    void deleteByUserId(@Param("userId") UUID userId);
}
