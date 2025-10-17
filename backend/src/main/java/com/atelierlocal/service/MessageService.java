package com.atelierlocal.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.atelierlocal.dto.ConversationSummaryDTO;
import com.atelierlocal.dto.MessageRequestDTO;
import com.atelierlocal.dto.MessageResponseDTO;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Attachment;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.Message;
import com.atelierlocal.model.S3Properties;
import com.atelierlocal.model.User;
import com.atelierlocal.repository.AttachmentRepo;
import com.atelierlocal.repository.MessageRepo;
import com.atelierlocal.repository.UserRepo;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class MessageService {

    private static final List<String> ALLOWED_FILE_TYPES = List.of("image/png", "image/jpeg", "application/pdf");
    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024; // 15 Mo

    private final MessageRepo messageRepo;
    private final UserRepo userRepo;
    private final AttachmentRepo attachmentRepo;
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public MessageService(MessageRepo messageRepo, UserRepo userRepo, 
                         AttachmentRepo attachmentRepo, S3Client s3Client, S3Properties s3Properties) {
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
        this.attachmentRepo = attachmentRepo;
        this.s3Client = s3Client;
        this.s3Properties = s3Properties;
    }

    @Transactional
    public MessageResponseDTO sendMessage(@Valid MessageRequestDTO dto) {
        try {
            User sender = userRepo.findById(dto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Expéditeur non trouvé avec l'ID: " + dto.getSenderId()));
            User receiver = userRepo.findById(dto.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("Destinataire non trouvé avec l'ID: " + dto.getReceiverId()));

            Message message = new Message();
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(dto.getContent());
            message.setMessageStatus(com.atelierlocal.model.MessageStatus.DELIVERED);

            // Sauvegarder le message
            Message savedMessage = messageRepo.save(message);

            // Gérer l'upload et la sauvegarde de l'attachment
            if (dto.getFile() != null && !dto.getFile().isEmpty()) {
                try {
                    Attachment attachment = uploadToS3(dto.getFile());
                    attachment.setMessage(savedMessage);
                    attachmentRepo.save(attachment);
                    savedMessage.getAttachments().add(attachment);
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'upload du fichier: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Initialiser les relations nécessaires
            Hibernate.initialize(savedMessage.getSender());
            Hibernate.initialize(savedMessage.getReceiver());
            Hibernate.initialize(savedMessage.getAttachments());
            // Initialiser les relations imbriquées si nécessaire
            if (savedMessage.getReceiver() instanceof Client) {
                Client client = (Client) savedMessage.getReceiver();
                Hibernate.initialize(client.getAsking());
            }

            return new MessageResponseDTO(savedMessage);
        } catch (IllegalArgumentException e) {
            MessageResponseDTO response = new MessageResponseDTO(e.getMessage());
            response.setMessageStatus(com.atelierlocal.model.MessageStatus.NOT_SENT);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            MessageResponseDTO response = new MessageResponseDTO("Erreur inattendue lors de l'envoi du message: " + e.getMessage());
            response.setMessageStatus(com.atelierlocal.model.MessageStatus.NOT_SENT);
            return response;
        }
    }

    private Attachment uploadToS3(MultipartFile file) {
        validateFile(file);

        String key = String.format("messages/%s_%s", UUID.randomUUID(), file.getOriginalFilename());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(key)
                .contentType(file.getContentType())
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload du fichier sur S3: " + e.getMessage(), e);
        }

        String url = String.format("https://%s.s3.%s.amazonaws.com/%s",
                s3Properties.getBucketName(),
                s3Properties.getRegion(),
                key);

        Attachment attachment = new Attachment();
        attachment.setFileUrl(url);
        attachment.setFileType(file.getContentType());
        return attachment;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier fourni");
        }
        if (!ALLOWED_FILE_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Type de fichier non autorisé: " + file.getContentType() + 
                    ". Types autorisés: " + String.join(", ", ALLOWED_FILE_TYPES));
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Fichier trop volumineux: " + file.getSize() + 
                    " octets. Taille maximale: " + MAX_FILE_SIZE + " octets.");
        }
    }

    @Transactional
    public List<MessageResponseDTO> getConversation(UUID user1Id, UUID user2Id) {
        try {
            validateUserIds(user1Id, user2Id);
            List<Message> messages = messageRepo
                .findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAtAsc(user1Id, user2Id, user1Id, user2Id);
            // Initialiser les relations pour chaque message
            messages.forEach(message -> {
                Hibernate.initialize(message.getSender());
                Hibernate.initialize(message.getReceiver());
                Hibernate.initialize(message.getAttachments());
                if (message.getReceiver() instanceof Client) {
                    Client client = (Client) message.getReceiver();
                    Hibernate.initialize(client.getAsking());
                }
            });
            return messages.stream()
                    .map(MessageResponseDTO::new)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Erreur lors de la récupération de la conversation: " + e.getMessage());
        }
    }

    private void validateUserIds(UUID user1Id, UUID user2Id) {
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("Les IDs des utilisateurs ne peuvent pas être nuls");
        }
        if (!userRepo.existsById(user1Id)) {
            throw new IllegalArgumentException("Utilisateur avec l'ID " + user1Id + " non trouvé");
        }
        if (!userRepo.existsById(user2Id)) {
            throw new IllegalArgumentException("Utilisateur avec l'ID " + user2Id + " non trouvé");
        }
    }

    @Transactional
    public List<ConversationSummaryDTO> getConversationSummaries(UUID userId) {
        try {
            validateUserIds(userId, userId);
            List<Message> messages = messageRepo.findAllByUserId(userId);

            Map<UUID, Message> latestMessages = messages.stream()
                    .collect(Collectors.toMap(
                            m -> m.getSender().getId().equals(userId) ? m.getReceiver().getId() : m.getSender().getId(),
                            m -> m,
                            (m1, m2) -> m1.getCreatedAt().isAfter(m2.getCreatedAt()) ? m1 : m2
                    ));

            return latestMessages.values().stream()
                    .map(m -> {
                        // Initialiser les relations
                        Hibernate.initialize(m.getSender());
                        Hibernate.initialize(m.getReceiver());
                        User otherUser = m.getSender().getId().equals(userId) ? m.getReceiver() : m.getSender();
                        String otherUserName = getUserDisplayName(otherUser);
                        String otherUserRole = otherUser.getUserRole().name();

                        return new ConversationSummaryDTO(
                                otherUser.getId(),
                                otherUserName,
                                otherUserRole,
                                m.getContent(),
                                m.getCreatedAt()
                        );
                    })
                    .sorted((dto1, dto2) -> dto2.getLastTimestamp().compareTo(dto1.getLastTimestamp()))
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Erreur lors de la récupération des résumés de conversation: " + e.getMessage());
        }
    }

    private String getUserDisplayName(User user) {
        if (user instanceof Artisan artisan) {
            return artisan.getName() != null ? artisan.getName() : "Artisan sans nom";
        } else if (user instanceof Client client) {
            String firstName = client.getFirstName() != null ? client.getFirstName() : "";
            String lastName = client.getLastName() != null ? client.getLastName() : "";
            return (firstName + " " + lastName).trim().isEmpty() ? "Client sans nom" : (firstName + " " + lastName).trim();
        }
        throw new IllegalStateException("Type d'utilisateur inconnu: " + user.getClass().getSimpleName());
    }
}