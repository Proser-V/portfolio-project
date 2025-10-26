package com.atelierlocal.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO représentant un résumé de conversation pour l'interface utilisateur.
 * 
 * Ce DTO contient les informations essentielles d'une conversation avec un autre utilisateur :
 * son identité, son rôle, son avatar, le dernier message, la date de ce message,
 * et le nombre de messages non lus.
 */
public class ConversationSummaryDTO {

    // -------------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------------

    /**
     * Identifiant de l'autre utilisateur dans la conversation
     */
    private UUID otherUserId;

    /**
     * Nom complet ou affiché de l'autre utilisateur
     */
    private String otherUserName;

    /**
     * Rôle de l'autre utilisateur (ex : CLIENT, ARTISAN, ADMIN)
     */
    private String otherUserRole;

    /**
     * URL de l'avatar de l'autre utilisateur
     */
    private String otherUserAvatarUrl;

    /**
     * Contenu du dernier message échangé dans la conversation
     */
    private String lastMessage;

    /**
     * Date et heure du dernier message
     * Sérialisé dans le JSON avec la clé "timestamp"
     */
    @JsonProperty("timestamp")
    private LocalDateTime createdAt;

    /**
     * Nombre de messages non lus dans la conversation pour l'utilisateur courant
     */
    private long unreadCount;

    // -------------------------------------------------------------------------
    // CONSTRUCTEUR
    // -------------------------------------------------------------------------

    /**
     * Constructeur complet pour initialiser toutes les informations de résumé
     * d'une conversation.
     * 
     * @param otherUserId identifiant de l'autre utilisateur
     * @param otherUserName nom ou pseudo de l'autre utilisateur
     * @param otherUserRole rôle de l'autre utilisateur
     * @param otherUserAvatarUrl URL de l'avatar de l'autre utilisateur
     * @param lastMessage contenu du dernier message
     * @param createdAt date et heure du dernier message
     * @param unreadCount nombre de messages non lus
     */
    public ConversationSummaryDTO(
                                UUID otherUserId,
                                String otherUserName,
                                String otherUserRole,
                                String otherUserAvatarUrl,
                                String lastMessage,
                                LocalDateTime createdAt,
                                Long unreadCount
                                ) {
        this.otherUserId = otherUserId;
        this.otherUserName = otherUserName;
        this.otherUserRole = otherUserRole;
        this.otherUserAvatarUrl = otherUserAvatarUrl;
        this.lastMessage = lastMessage;
        this.createdAt = createdAt;
        this.unreadCount = unreadCount;
    }

    // -------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // -------------------------------------------------------------------------

    public UUID getOtherUserId() { return otherUserId; }
    public String getOtherUserName() { return otherUserName; }
    public String getOtherUserRole() { return otherUserRole; }
    public String getOtherUserAvatar() { return otherUserAvatarUrl; }
    public String getLastMessage() { return lastMessage; }
    public LocalDateTime getLastTimestamp() { return createdAt; }

    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }
}
