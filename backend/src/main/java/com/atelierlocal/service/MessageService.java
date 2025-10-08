package com.atelierlocal.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.atelierlocal.dto.MessageRequestDTO;
import com.atelierlocal.dto.MessageResponseDTO;
import com.atelierlocal.model.Attachment;
import com.atelierlocal.model.Message;
import com.atelierlocal.model.S3Properties;
import com.atelierlocal.model.User;
import com.atelierlocal.repository.MessageRepo;
import com.atelierlocal.repository.UserRepo;

import jakarta.transaction.Transactional;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

@Service
public class MessageService {

    private final MessageRepo messageRepo;
    private final UserRepo userRepo;
    private final AttachmentService attachmentService;
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public MessageService(MessageRepo messageRepo, UserRepo userRepo, AttachmentService attachmentService, S3Client s3Client, S3Properties s3Properties) {
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
        this.attachmentService = attachmentService;
        this.s3Client = s3Client;
        this.s3Properties = s3Properties;
    }

    @Transactional
    public MessageResponseDTO sendMessage(MessageRequestDTO dto) {
        User sender = userRepo.findById(dto.getSenderId())
            .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));
        User receiver = userRepo.findById(dto.getReceiverId())
            .orElseThrow(() -> new RuntimeException("Destinataire non trouvé"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(dto.getContent());
        message.setTimestamp(LocalDateTime.now());

        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            Attachment attachment = uploadToS3(dto.getFile());
            attachmentService.linkToMessage(attachment, message);
        }

        messageRepo.save(message);

        return new MessageResponseDTO(message);
    }

    private Attachment uploadToS3(MultipartFile file) {
    List<String> allowedTypes = List.of("image/png", "image/jpeg", "application/pdf");
    if (!allowedTypes.contains(file.getContentType())) {
        throw new IllegalArgumentException("Type de fichier non autorisé : " + file.getContentType());
    }
    if (file.getSize() > 15 * 1024 * 1024) {
        throw new IllegalArgumentException("Fichier trop volumineux (max 15 Mo).");
    }

    String key = "messages/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(s3Properties.getBucketName())
            .key(key)
            .acl("public-read")
            .contentType(file.getContentType())
            .build();

    InputStream inputStream = null;
    try {
        inputStream = file.getInputStream();
        s3Client.putObject(
            putObjectRequest,
            RequestBody.fromInputStream(inputStream, file.getSize()));
    } catch (IOException e) {
        throw new RuntimeException("Erreur lors de l'upload du fichier sur S3", e);
    } finally {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ignored) {
                // On ignore la fermeture échouée
            }
        }
    }

    String url = String.format(
        "https://%s.s3.%s.amazonaws.com/%s",
        s3Properties.getBucketName(),
        s3Properties.getRegion(),
        key);

    Attachment attachment = new Attachment();
    attachment.setFileUrl(url);
    attachment.setFileType(file.getContentType());
    return attachment;
    }

    public List<MessageResponseDTO> getConversation(UUID user1Id, UUID user2Id) {
        List<Message> messages = messageRepo
            .findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(user1Id, user2Id, user1Id, user2Id);
        return messages.stream()
                       .map(MessageResponseDTO::new)
                       .collect(Collectors.toList());
    }
}