package com.atelierlocal.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.atelierlocal.repository.AttachmentRepo;
import com.atelierlocal.repository.MessageRepo;

import java.time.LocalDateTime;

@Service
public class MessageService {

    private final MessageRepo messageRepo;
    private final AttachmentRepo attachmentRepo;

    public MessageService(MessageRepo messageRepo, AttachmentRepo attachmentRepo) {
        this.messageRepo = messageRepo;
        this.attachmentRepo = attachmentRepo;
    }

    public Message sendMessage(User sender, User receiver, String content, List<MultipartFile> files) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        // Gestion des fichiers
        if (files != null) {
            for (MultipartFile file : files) {
                String fileUrl = saveFile(file);

                Attachment attchment = new Attachment();
                attachment.setFileUrl(fileUrl);
                attachment.setFileType(file.getContentType());
                attachment.setFileSize(file.getSize());

                message.addAttachment(attachment);
            }
        }

        return messageRepository.save(message);
    }

    private String saveFile(MultipartFile file) {
        return "/uploads" + file.getOriginalFilename();
    }
}
