package com.atelierlocal.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atelierlocal.model.Message;
import com.atelierlocal.model.User;

/**
 * Repository pour l'entité Message.
 * 
 * Ce repository gère les opérations de persistance liées aux messages échangés entre utilisateurs.
 * Il hérite de JpaRepository, offrant ainsi toutes les méthodes CRUD standard :
 *   - save(), saveAll(): persister ou mettre à jour des messages
 *   - findById(), findAll(), findAllById(): récupérer des messages
 *   - existsById(): vérifier l'existence d'un message
 *   - delete(), deleteById(), deleteAll(): supprimer des messages
 *   - count(): compter le nombre total de messages
 * 
 * Méthodes personnalisées :
 *   - findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAtAsc(...):
 *       Récupère la conversation complète entre deux utilisateurs (dans les deux sens), triée chronologiquement.
 *   - findByReceiverAndIsReadFalse(User receiver):
 *       Récupère tous les messages non lus pour un utilisateur donné (boîte de réception).
 *   - findAllBySenderIdOrReceiverId(UUID senderId, UUID receiverId):
 *       Récupère tous les messages envoyés ou reçus par un utilisateur donné.
 * 
 * Bonnes pratiques :
 *   - Toujours trier les résultats par date lors de l'affichage d'une conversation pour garantir la cohérence de l'ordre.
 *   - Penser à marquer les messages comme lus via une mise à jour de l'attribut "isRead" après consultation.
 */

@Repository
public interface MessageRepo extends JpaRepository<Message, UUID> {
    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAtAsc(
        UUID senderId, UUID receiverId, UUID senderId2, UUID receiverId2
    );

    List<Message> findByReceiverAndIsReadFalse(User receiver);

    List<Message> findAllBySenderIdOrReceiverId(UUID senderId, UUID receiverId);
}
