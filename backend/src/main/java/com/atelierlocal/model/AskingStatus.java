package com.atelierlocal.model;

/**
 * Enumération représentant les différents états possibles d'une demande (Asking).
 * 
 * PENDING   : La demande a été créée mais n'a pas encore été traitée.
 * DONE      : La demande a été traitée ou complétée avec succès.
 * CANCELLED : La demande a été annulée.
 */
public enum AskingStatus {
    PENDING,
    DONE,
    CANCELLED
}
