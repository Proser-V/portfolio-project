package com.atelierlocal.dto;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotNull;

/**
 * DTO utilisé pour la création d'un message entre deux utilisateurs.
 * 
 * Ce DTO contient les informations nécessaires pour envoyer un message :
 * - senderId : l'ID de l'utilisateur qui envoie le message
 * - receiverId : l'ID de l'utilisateur destinataire
 * - content : le texte du message (optionnel si un fichier est attaché)
 * - file : un fichier attaché au message (optionnel)
 * - tempId : identifiant temporaire utilisé côté client pour le suivi du message avant son enregistrement en base
 * 
 * Il est utilisé dans les endpoints de messagerie pour transmettre les données du message.
 */
public class MessageRequestDTO {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /** ID de l'envoyeur du message */
    @NotNull(message = "L'ID de l'envoyeur est requise")
    private UUID senderId;

    /** ID du destinataire du message */
    @NotNull(message = "L'ID du destinataire est requise")
    private UUID receiverId;

    /** Contenu textuel du message */
    private String content;

    /** Fichier attaché au message */
    private MultipartFile file;

    /** Identifiant temporaire pour le suivi côté client */
    private String tempId;

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public UUID getSenderId() { return senderId; }
    public void setSenderId(UUID senderId) { this.senderId = senderId; }

    public UUID getReceiverId() { return receiverId; }
    public void setReceiverId(UUID receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }

    public String getTempId() { return tempId; }
    public void setTempId(String tempId) { this.tempId = tempId; }
}
