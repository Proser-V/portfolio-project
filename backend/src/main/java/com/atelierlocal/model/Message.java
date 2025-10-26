package com.atelierlocal.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/**
 * Entité représentant un message échangé entre utilisateurs.
 * 
 * Cette classe permet de stocker les informations d'un message :
 * - expéditeur et destinataire
 * - contenu texte
 * - pièces jointes associées
 * - statut de lecture
 * - erreurs éventuelles
 * - statut du message
 * - dates de création et mise à jour automatiques
 * - identifiant temporaire (pour gestion front-end)
 */
@Entity
public class Message {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /**
     * Identifiant unique du message.
     * Généré automatiquement et non modifiable.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Utilisateur expéditeur du message.
     * Relation ManyToOne vers User, obligatoire.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * Utilisateur destinataire du message.
     * Relation ManyToOne vers User, obligatoire.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    /**
     * Contenu texte du message.
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Liste des pièces jointes associées au message.
     * Relation OneToMany vers Attachment, cascade sur toutes les opérations.
     */
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    /**
     * Indique si le message a été lu par le destinataire.
     */
    @Column(nullable = false)
    private boolean isRead = false;

    /**
     * Champ pour stocker une éventuelle erreur liée au message.
     */
    private String messageError;

    /**
     * Statut du message (ex : SENT, DELIVERED, READ).
     * Enumération stockée en tant que chaîne.
     */
    @Enumerated(EnumType.STRING)
    private MessageStatus messageStatus = MessageStatus.SENT;

    /**
     * Date et heure de création du message.
     * Remplie automatiquement lors de l'insertion.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date et heure de la dernière mise à jour du message.
     * Mise à jour automatiquement à chaque modification.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Identifiant temporaire pour la gestion front-end ou synchronisation.
     */
    private String tempId;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean getRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }

    public List<Attachment> getAttachments() { return attachments; }
    public void setAttachments(List<Attachment> attachments) { this.attachments = attachments; }

    public String getMessageError() { return messageError; }
    public void setMessageError(String messageError) { this.messageError = messageError; }

    public MessageStatus getMessageStatus() { return messageStatus; }
    public void setMessageStatus(MessageStatus messageStatus) { this.messageStatus = messageStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getTempId() { return tempId; }
    public void setTempId(String tempId) { this.tempId = tempId; }
}
