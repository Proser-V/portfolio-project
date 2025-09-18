package com.atelierlocal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.atelierlocal.model.Attachment;
import com.atelierlocal.model.Message;
import com.atelierlocal.model.User;
import com.atelierlocal.repository.AttachmentRepo;
import com.atelierlocal.repository.MessageRepo;

@Service
public class MessageService {

    private final MessageRepo messageRepo;
    private final AttachmentService attachmentService;

    public MessageService(MessageRepo messageRepo, AttachmentRepo attachmentRepo, AttachmentService attachmentService) {
        this.messageRepo = messageRepo;
        this.attachmentService = attachmentService;
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

                Attachment attachment = new Attachment();
                attachment.setFileUrl(fileUrl);
                attachment.setFileType(file.getContentType());
                attachment.setFileSize(file.getSize());

                attachmentService.linkToMessage(attachment, message);
            }
        }

        return messageRepo.save(message);
    }

    private String saveFile(MultipartFile file) {
        return "/uploads/" + file.getOriginalFilename();
    }
}
