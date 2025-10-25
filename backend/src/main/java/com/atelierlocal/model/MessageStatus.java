package com.atelierlocal.model;

/**
 * Enumération représentant le statut d'un message.
 * 
 * Les statuts possibles sont :
 * - SENT : le message a été envoyé
 * - DELIVERED : le message a été remis au destinataire
 * - FAILED : l'envoi du message a échoué
 * 
 * Utilisé dans l'entité Message pour indiquer l'état actuel du message.
 */
public enum MessageStatus {
    SENT,
    DELIVERED,
    FAILED
}
