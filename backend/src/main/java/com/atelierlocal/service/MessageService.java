package com.atelierlocal.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.AttachmentRepo;
import com.atelierlocal.repository.ClientRepo;
import com.atelierlocal.repository.MessageRepo;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class MessageService {

    private static final List<String> ALLOWED_FILE_TYPES = List.of("image/png", "image/jpeg", "application/pdf");
    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024; // 15 Mo

    private final MessageRepo messageRepository;
    private final ArtisanRepo artisanRepo;
    private final ClientRepo clientRepo;
    private final S3Client s3Client;
    private final S3Properties s3Properties;
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    public MessageService(MessageRepo messageRepository, ArtisanRepo artisanRepo, ClientRepo clientRepo,
                         S3Client s3Client, S3Properties s3Properties,
                         SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.artisanRepo = artisanRepo;
        this.clientRepo = clientRepo;
        this.s3Client = s3Client;
        this.s3Properties = s3Properties;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public MessageResponseDTO sendMessage(@Valid MessageRequestDTO dto) {
        try {
            User sender = findUserById(dto.getSenderId());
            User receiver = findUserById(dto.getReceiverId());

            Message message = new Message();
        
        // ✅ CORRECTION : Définir sender et receiver AVANT la sauvegarde
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(dto.getContent());
            message.setRead(false);
            message.setTempId(dto.getTempId());
            message.setMessageStatus(com.atelierlocal.model.MessageStatus.SENT);

        // Gestion des pièces jointes
            if (dto.getFile() != null && !dto.getFile().isEmpty()) {
                Attachment attachment = uploadToS3(dto.getFile());
                attachment.setMessage(message);
                message.getAttachments().add(attachment);
            // Ne pas sauvegarder l'attachment séparément, cascade le fera
            }

        // Sauvegarder le message (cascade sauvegarde les attachments)
            Message savedMessage = messageRepository.save(message);

        // Notifier le destinataire des messages non lus
            notifyUnreadMessages(receiver);

        // Retourner la réponse
            MessageResponseDTO response = new MessageResponseDTO(savedMessage);
            return response;

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            MessageResponseDTO response = new MessageResponseDTO(e.getMessage());
            response.setMessageStatus(com.atelierlocal.model.MessageStatus.FAILED);
            response.setTempId(dto.getTempId());
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            MessageResponseDTO response = new MessageResponseDTO("Erreur serveur interne lors de l'envoi: " + e.getMessage());
            response.setMessageStatus(com.atelierlocal.model.MessageStatus.FAILED);
            response.setTempId(dto.getTempId());
            return response;
        }
    }

    private User findUserById(UUID userId) {
        return artisanRepo.findById(userId)
            .map(User.class::cast)
            .orElseGet(() -> clientRepo.findById(userId)
                .map(User.class::cast)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + userId)));
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
            throw new IllegalArgumentException("Erreur lors de l'upload du fichier sur S3: " + e.getMessage(), e);
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
            List<Message> messages = messageRepository
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
        if (!artisanRepo.existsById(user1Id) && !clientRepo.existsById(user1Id)) {
            throw new IllegalArgumentException("Utilisateur avec l'ID " + user1Id + " non trouvé");
        }
        if (!artisanRepo.existsById(user2Id) && !clientRepo.existsById(user2Id)) {
            throw new IllegalArgumentException("Utilisateur avec l'ID " + user2Id + " non trouvé");
        }
    }

    @Transactional
    public List<ConversationSummaryDTO> getConversationSummaries(UUID userId) {
        logger.info("🔍 Récupération des conversations pour userId: {}", userId);
    
        List<Message> messages = messageRepository.findAllBySenderIdOrReceiverId(userId, userId);
        logger.info("📊 Messages trouvés: {}", messages.size());
        logger.info("📊 Détails des messages: {}", messages);
    
        messages.forEach(m -> {
            Hibernate.initialize(m.getSender());
            Hibernate.initialize(m.getReceiver());
            Hibernate.initialize(m.getSender().getAvatar());
            Hibernate.initialize(m.getReceiver().getAvatar());
        });
    
        User currentUser = findUserById(userId); 
        logger.info("📊 Utilisateur courant: {}", currentUser.getEmail());
    
        List<Message> allUnreadMessages = messageRepository.findByReceiverAndIsReadFalse(currentUser); 
        logger.info("📊 Messages non lus: {}", allUnreadMessages.size());
        logger.info("📊 Détails des messages non lus: {}", allUnreadMessages);
    
        Map<UUID, Long> unreadCountsBySender = allUnreadMessages.stream()
            .collect(Collectors.groupingBy( 
                msg -> msg.getSender().getId(), 
                Collectors.counting() 
            ));
        logger.info("📊 Comptage des messages non lus par expéditeur: {}", unreadCountsBySender);
    
        Map<UUID, Message> latestMessages = messages.stream()
                .collect(Collectors.toMap(
                        m -> m.getSender().getId().equals(userId) ? m.getReceiver().getId() : m.getSender().getId(),
                        m -> m,
                        (m1, m2) -> m1.getCreatedAt().isAfter(m2.getCreatedAt()) ? m1 : m2
                ));
        logger.info("📊 Derniers messages par conversation: {}", latestMessages.size());
    
        List<ConversationSummaryDTO> summaries = latestMessages.values().stream()
                .map(m -> {
                    User otherUser = m.getSender().getId().equals(userId) ? m.getReceiver() : m.getSender();
                    String otherUserName = getUserDisplayName(otherUser);
                    String otherUserRole = otherUser.getUserRole().name();
                    String otherUserAvatarUrl = otherUser.getAvatar() != null ? otherUser.getAvatar().getAvatarUrl() : null;
                    long unreadCount = unreadCountsBySender.getOrDefault(otherUser.getId(), 0L);
                    return new ConversationSummaryDTO(
                        otherUser.getId(),
                        otherUserName,
                        otherUserRole,
                        otherUserAvatarUrl,
                        m.getContent(),
                        m.getCreatedAt(),
                        unreadCount
                    );
                })
                .sorted((dto1, dto2) -> dto2.getLastTimestamp().compareTo(dto1.getLastTimestamp()))
                .collect(Collectors.toList());
    
        logger.info("📊 Résumés de conversations renvoyés: {}", summaries.size());
        logger.info("📊 Détails des résumés: {}", summaries);
        return summaries;
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

    // Récupérer les messages non lus pour un utilisateur
    @Transactional
    public List<Message> getUnreadMessages(User receiver) {
        return messageRepository.findByReceiverAndIsReadFalse(receiver);
    }

    // Marquer un message comme lu
    @Transactional
    public void markMessageAsRead(UUID messageId, User authenticatedUser) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("Message non trouvé : " + messageId));
        if (!message.getReceiver().getId().equals(authenticatedUser.getId())) {
            throw new IllegalArgumentException("Seul le destinataire peut marquer le message comme lu");
        }
        message.setRead(true);
        messageRepository.save(message);
        // Notifier le destinataire du nouveau nombre de messages non lus
        notifyUnreadMessages(authenticatedUser);
    }

    // Notifier l'utilisateur du nombre de messages non lus
    private void notifyUnreadMessages(User receiver) {
        List<Message> unreadMessages = getUnreadMessages(receiver);
        messagingTemplate.convertAndSendToUser(
            receiver.getEmail(),
            "/queue/unread",
            unreadMessages.size()
        );
    }
}