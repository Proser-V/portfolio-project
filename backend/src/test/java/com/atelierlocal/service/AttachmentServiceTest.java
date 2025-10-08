package com.atelierlocal.service;

import com.atelierlocal.model.Attachment;
import com.atelierlocal.model.Message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AttachmentServiceTest {
    
    private AttachmentService attachmentService;

    @BeforeEach
    void setup() {
        attachmentService = new AttachmentService();
    }

    @Test
    void linkToMessage() {
        Message message = new Message();
        message.setAttachments(new ArrayList<>());

        Attachment attachment = new Attachment();

        attachmentService.linkToMessage(attachment, message);

        assertTrue(message.getAttachments().contains(attachment),
            "La pièce jointe doit être ajoutée au message");
        assertEquals(message, attachment.getMessage(),
            "Le message doit être défini avec la pièce jointe");
    }

    @Test
    void unlinkFromMessage() {
        Message message = new Message();
        message.setAttachments(new ArrayList<>());

        Attachment attachment = new Attachment();

        attachmentService.linkToMessage(attachment, message);
        assertTrue(message.getAttachments().contains(attachment));

        attachmentService.unlinkFromMessage(attachment, message);

        assertFalse(message.getAttachments().contains(attachment),
            "La pièce jointe doit être retirée du message");
        assertNull(attachment.getMessage(),
            "Le message doit être égal à null dans la pièce jointe.");
    }
}
