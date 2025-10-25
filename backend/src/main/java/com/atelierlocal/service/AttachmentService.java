package com.atelierlocal.service;

import org.springframework.stereotype.Service;

import com.atelierlocal.model.Attachment;
import com.atelierlocal.model.Message;

/**
 * Service pour gérer la liaison entre les pièces jointes (Attachment) et les messages.
 * 
 * Fournit des méthodes pour :
 * - lier une pièce jointe à un message,
 * - délier une pièce jointe d'un message.
 */
@Service
public class AttachmentService {

    /**
     * Lie une pièce jointe à un message.
     * 
     * @param attachment pièce jointe à lier
     * @param message message auquel lier la pièce jointe
     */
    public void linkToMessage(Attachment attachment, Message message) {
        message.getAttachments().add(attachment);
        attachment.setMessage(message);
    }

    /**
     * Délie une pièce jointe d'un message.
     * 
     * @param attachment pièce jointe à délier
     * @param message message dont on retire la pièce jointe
     */
    public void unlinkFromMessage(Attachment attachment, Message message) {
        message.getAttachments().remove(attachment);
        attachment.setMessage(null);
    }
}
