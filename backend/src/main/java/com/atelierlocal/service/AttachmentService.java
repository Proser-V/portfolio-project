package com.atelierlocal.service;

import org.springframework.stereotype.Service;

import com.atelierlocal.model.Attachment;
import com.atelierlocal.model.Message;

@Service
public class AttachmentService {

    public void linkToMessage(Attachment attachment, Message message) {
        message.getAttachments().add(attachment);
        attachment.setMessage(message);
    }

    public void unlinkFromMessage(Attachment attachment, Message message) {
        message.getAttachments().remove(attachment);
        attachment.setMessage(null);
    }
}
