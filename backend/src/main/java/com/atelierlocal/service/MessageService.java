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
import com.atelierlocal.repository.ClientRepo;
import com.atelierlocal.repository.MessageRepo;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Service de gestion des messages entre utilisateurs (Artisans et Clients).
 * 
 * Fournit des fonctionnalités pour :
 * - envoyer un message avec ou sans pièce jointe,
 * - récupérer la conversation entre deux utilisateurs,
 * - récupérer les résumés des conversations,
 * - gérer les pièces jointes avec AWS S3,
 * - notifier les utilisateurs des messages non lus.
 */
@Service
public class MessageService {

    // Types de fichiers autorisés pour les pièces jointes
    private static final List<String> ALLOWED_FILE_TYPES = List.of("image/png", "image/jpeg", "application/pdf");
    // Taille maximale d'un fichier (5 Mo)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final MessageRepo messageRepository;
    private final ArtisanRepo artisanRepo;
    private final ClientRepo clientRepo;
    private final S3Client s3Client;
    private final S3Properties s3Properties;
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    /**
     * Constructeur du service.
     *
     * @param messageRepository repository des messages
     * @param artisanRepo repository des artisans
     * @param clientRepo repository des clients
     * @param s3Client client AWS S3 pour l'upload des pièces jointes
     * @param s3Properties propriétés S3 (bucket, région)
     * @param messagingTemplate pour la notification en temps réel via WebSocket
     */
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

    /**
     * Envoie un message d'un utilisateur à un autre.
     *
     * @param dto DTO contenant les informations du message (expéditeur, destinataire, contenu, pièce jointe)
     * @return MessageResponseDTO avec le message sauvegardé et son statut
     */
    @Transactional
    public MessageResponseDTO sendMessage(@Valid MessageRequestDTO dto) {
        try {
            // Récupération des utilisateurs expéditeur et destinataire
            User sender = findUserById(dto.getSenderId());
            User receiver = findUserById(dto.getReceiverId());

            // Création de l'entité Message
            Message message = new Message();
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(dto.getContent());
            message.setRead(false);
            message.setTempId(dto.getTempId());
            message.setMessageStatus(com.atelierlocal.model.MessageStatus.SENT);

            // Gestion des pièces jointes si présentes
            if (dto.getFile() != null && !dto.getFile().isEmpty()) {
                Attachment attachment = uploadToS3(dto.getFile());
                attachment.setMessage(message);
                message.getAttachments().add(attachment);
            }

            // Sauvegarde du message (les attachments sont sauvegardés en cascade)
            Message savedMessage = messageRepository.save(message);

            // Notification du destinataire pour les messages non lus
            notifyUnreadMessages(receiver);

            // Conversion en DTO pour la réponse
            MessageResponseDTO response = new MessageResponseDTO(savedMessage);
            return response;

        } catch (IllegalArgumentException e) {
            // Gestion des erreurs liées à la saisie ou aux entités
            e.printStackTrace();
            MessageResponseDTO response = new MessageResponseDTO(e.getMessage());
            response.setMessageStatus(com.atelierlocal.model.MessageStatus.FAILED);
            response.setTempId(dto.getTempId());
            return response;

        } catch (Exception e) {
            // Gestion des erreurs serveur inattendues
            e.printStackTrace();
            MessageResponseDTO response = new MessageResponseDTO("Erreur serveur interne lors de l'envoi: " + e.getMessage());
            response.setMessageStatus(com.atelierlocal.model.MessageStatus.FAILED);
            response.setTempId(dto.getTempId());
            return response;
        }
    }

    /**
     * Récupère un utilisateur par son ID en vérifiant s'il est artisan ou client.
     *
     * @param userId ID de l'utilisateur
     * @return User trouvé
     */
    private User findUserById(UUID userId) {
        return artisanRepo.findById(userId)
            .map(User.class::cast)
            .orElseGet(() -> clientRepo.findById(userId)
                .map(User.class::cast)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + userId)));
    }

    /**
     * Upload d'une pièce jointe sur AWS S3.
     *
     * @param file fichier multipart
     * @return Attachment contenant l'URL et le type de fichier
     */
    private Attachment uploadToS3(MultipartFile file) {
        validateFile(file);

        // Génération de la clé unique pour le fichier
        String key = String.format("messages/%s_%s", UUID.randomUUID(), file.getOriginalFilename());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(key)
                .contentType(file.getContentType())
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            // Upload du fichier vers S3
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException e) {
            throw new IllegalArgumentException("Erreur lors de l'upload du fichier sur S3: " + e.getMessage(), e);
        }

        // Construction de l'URL publique du fichier
        String url = String.format("https://%s.s3.%s.amazonaws.com/%s",
                s3Properties.getBucketName(),
                s3Properties.getRegion(),
                key);

        // Création de l'entité Attachment
        Attachment attachment = new Attachment();
        attachment.setFileUrl(url);
        attachment.setFileType(file.getContentType());
        return attachment;
    }

    /**
     * Validation des fichiers joints avant l'upload.
     *
     * @param file fichier multipart
     */
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

    /**
     * Récupère la conversation entre deux utilisateurs.
     *
     * @param user1Id ID du premier utilisateur
     * @param user2Id ID du second utilisateur
     * @return Liste de MessageResponseDTO triée par date croissante
     */
    @Transactional
    public List<MessageResponseDTO> getConversation(UUID user1Id, UUID user2Id) {
        try {
            validateUserIds(user1Id, user2Id);
            List<Message> messages = messageRepository
                .findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAtAsc(user1Id, user2Id, user1Id, user2Id);

            // Initialisation des relations pour éviter LazyInitializationException
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

    /**
     * Validation de l'existence des utilisateurs dans la conversation.
     *
     * @param user1Id ID du premier utilisateur
     * @param user2Id ID du second utilisateur
     */
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

    /**
     * Récupère les résumés de toutes les conversations d'un utilisateur.
     *
     * @param userId ID de l'utilisateur courant
     * @return Liste de ConversationSummaryDTO triée par date du dernier message décroissante
     */
    @Transactional
    public List<ConversationSummaryDTO> getConversationSummaries(UUID userId) {
        logger.info("🔍 Récupération des conversations pour userId: {}", userId);

        // Récupération de tous les messages envoyés ou reçus
        List<Message> messages = messageRepository.findAllBySenderIdOrReceiverId(userId, userId);
        logger.info("📊 Messages trouvés: {}", messages.size());
        logger.info("📊 Détails des messages: {}", messages);

        // Initialisation des relations pour éviter LazyInitializationException
        messages.forEach(m -> {
            Hibernate.initialize(m.getSender());
            Hibernate.initialize(m.getReceiver());
            Hibernate.initialize(m.getSender().getAvatar());
            Hibernate.initialize(m.getReceiver().getAvatar());
        });

        User currentUser = findUserById(userId); 
        logger.info("📊 Utilisateur courant: {}", currentUser.getEmail());

        // Récupération des messages non lus
        List<Message> allUnreadMessages = messageRepository.findByReceiverAndIsReadFalse(currentUser); 
        logger.info("📊 Messages non lus: {}", allUnreadMessages.size());
        logger.info("📊 Détails des messages non lus: {}", allUnreadMessages);

        // Comptage des messages non lus par expéditeur
        Map<UUID, Long> unreadCountsBySender = allUnreadMessages.stream()
            .collect(Collectors.groupingBy( 
                msg -> msg.getSender().getId(), 
                Collectors.counting() 
            ));
        logger.info("📊 Comptage des messages non lus par expéditeur: {}", unreadCountsBySender);

        // Récupération des derniers messages par conversation
        Map<UUID, Message> latestMessages = messages.stream()
                .collect(Collectors.toMap(
                        m -> m.getSender().getId().equals(userId) ? m.getReceiver().getId() : m.getSender().getId(),
                        m -> m,
                        (m1, m2) -> m1.getCreatedAt().isAfter(m2.getCreatedAt()) ? m1 : m2
                ));
        logger.info("📊 Derniers messages par conversation: {}", latestMessages.size());

        // Construction des DTOs de résumé de conversation
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

    /**
     * Récupère le nom affichable d'un utilisateur selon son type (Artisan/Client).
     *
     * @param user utilisateur
     * @return nom affichable
     */
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

    /**
     * Récupère les messages non lus pour un utilisateur donné.
     *
     * @param receiver utilisateur destinataire
     * @return liste de messages non lus
     */
    @Transactional
    public List<Message> getUnreadMessages(User receiver) {
        return messageRepository.findByReceiverAndIsReadFalse(receiver);
    }

    /**
     * Marque un message comme lu.
     *
     * @param messageId ID du message
     * @param authenticatedUser utilisateur connecté (doit être le destinataire)
     */
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

    /**
     * Notifie un utilisateur du nombre de messages non lus via WebSocket.
     *
     * @param receiver utilisateur destinataire
     */
    private void notifyUnreadMessages(User receiver) {
        List<Message> unreadMessages = getUnreadMessages(receiver);
        messagingTemplate.convertAndSendToUser(
            receiver.getEmail(),
            "/queue/unread",
            unreadMessages.size()
        );
    }
}
