package com.atelierlocal.model;

/**
 * Enumération représentant le rôle d'un utilisateur.
 * 
 * Les rôles possibles sont :
 * - CLIENT : utilisateur classique pouvant faire des demandes et recommandations
 * - ARTISAN : utilisateur professionnel proposant ses services
 * - ADMIN : utilisateur avec droits d'administration et gestion de la plateforme
 * 
 * Utilisé dans l'entité User pour gérer l'accès et les autorisations.
 */
public enum UserRole {
    CLIENT,
    ARTISAN,
    ADMIN
}
