package com.atelierlocal.dto;

import java.util.List;
import java.util.UUID;

import com.atelierlocal.model.Attachment;
import com.atelierlocal.model.Message;
import com.atelierlocal.model.MessageStatus;

public class MessageResponseDTO {
    private UUID id;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private String messageError;
    private MessageStatus messageStatus;
    private List<Attachment> attachments;

    public MessageResponseDTO(Message message) {
        this.id = message.getId();
        this.senderId = message.getSender().getId();
        this.receiverId = message.getReceiver().getId();
        this.content = message.getContent();
        this.attachments = message.getAttachments();
        this.messageError = message.getMessageError();
        this.messageStatus = message.getMessageStatus();
    }

    // Getters et setters
    public UUID getId() { return id; }

    public UUID getSenderId() { return senderId; }
    public void setSenderId(UUID senderId) { this.senderId = senderId; }

    public UUID getReceiverId() { return receiverId; }
    public void setReceiverId(UUID receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setCOntent(String content) { this.content = content; }

    public String getMessageError() { return messageError; }
    public void setMessageError(String messageError) { this.messageError = messageError; }

    public MessageStatus getMessageStatus() { return messageStatus; }
    public void setMessageStatus(MessageStatus messageStatus) { this.messageStatus = messageStatus; }

    public List<Attachment> getAttachment() { return attachments; }
    public void setAttachment(List<Attachment> attachments) { this.attachments = attachments; }
}
