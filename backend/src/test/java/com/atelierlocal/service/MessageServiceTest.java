package com.atelierlocal.service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.atelierlocal.dto.ConversationSummaryDTO;
import com.atelierlocal.dto.MessageRequestDTO;
import com.atelierlocal.dto.MessageResponseDTO;
import com.atelierlocal.model.*;
import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.AttachmentRepo;
import com.atelierlocal.repository.ClientRepo;
import com.atelierlocal.repository.MessageRepo;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

class MessageServiceTest {

    @Mock
    private MessageRepo messageRepo;

    @Mock
    private ArtisanRepo artisanRepo;

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private AttachmentRepo attachmentRepo;

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Properties s3Properties;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendMessageWithoutFile() {
        // Arrange
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        String expectedContent = "Hello Artisan";

        Client sender = new Client();
        sender.setId(senderId);

        Artisan receiver = new Artisan();
        receiver.setId(receiverId);
        receiver.setEmail("receiver@test.com");

        when(artisanRepo.findById(senderId)).thenReturn(Optional.empty());
        when(clientRepo.findById(senderId)).thenReturn(Optional.of(sender));
        when(artisanRepo.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(clientRepo.findById(receiverId)).thenReturn(Optional.empty());

        Message savedMessage = new Message();
        savedMessage.setId(UUID.randomUUID());
        savedMessage.setContent(expectedContent);
        savedMessage.setSender(sender);
        savedMessage.setReceiver(receiver);
        savedMessage.setMessageStatus(MessageStatus.SENT);

        when(messageRepo.save(any(Message.class))).thenReturn(savedMessage);

        MessageRequestDTO dto = new MessageRequestDTO();
        dto.setSenderId(senderId);
        dto.setReceiverId(receiverId);
        dto.setContent(expectedContent);
        dto.setFile(null);

        // Act
        MessageResponseDTO response = messageService.sendMessage(dto);

        // Assert
        assertNotNull(response);
        assertEquals(expectedContent, response.getContent());
        assertEquals(MessageStatus.SENT, response.getMessageStatus());
        
        verify(messageRepo, times(1)).save(any(Message.class));
        verify(attachmentRepo, never()).save(any(Attachment.class));
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(messagingTemplate, times(1))
            .convertAndSendToUser(eq("receiver@test.com"), eq("/queue/unread"), any(Integer.class));
    }

    @Test
    void testSendMessageWithFile() throws Exception {
        // Arrange
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        String expectedContent = "Message with file";
        String fileName = "test.png";

        Client sender = new Client();
        sender.setId(senderId);

        Artisan receiver = new Artisan();
        receiver.setId(receiverId);
        receiver.setEmail("receiver@test.com");

        when(artisanRepo.findById(senderId)).thenReturn(Optional.empty());
        when(clientRepo.findById(senderId)).thenReturn(Optional.of(sender));
        when(artisanRepo.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(clientRepo.findById(receiverId)).thenReturn(Optional.empty());

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(1024L);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));

        when(s3Properties.getBucketName()).thenReturn("my-bucket");
        when(s3Properties.getRegion()).thenReturn("eu-west-1");

        Message savedMessage = new Message();
        savedMessage.setId(UUID.randomUUID());
        savedMessage.setContent(expectedContent);
        savedMessage.setSender(sender);
        savedMessage.setReceiver(receiver);
        savedMessage.setMessageStatus(MessageStatus.SENT);

        Attachment attachment = new Attachment();
        attachment.setFileUrl(String.format("https://my-bucket.s3.eu-west-1.amazonaws.com/messages/%s_%s", 
            UUID.randomUUID(), fileName));
        attachment.setFileType("image/png");
        attachment.setMessage(savedMessage);
        savedMessage.getAttachments().add(attachment);

        when(messageRepo.save(any(Message.class))).thenReturn(savedMessage);

        MessageRequestDTO dto = new MessageRequestDTO();
        dto.setSenderId(senderId);
        dto.setReceiverId(receiverId);
        dto.setContent(expectedContent);
        dto.setFile(file);

        // Act
        MessageResponseDTO response = messageService.sendMessage(dto);

        // Assert
        assertNotNull(response);
        assertEquals(expectedContent, response.getContent());
        assertEquals(1, response.getAttachments().size());

        String actualUrl = response.getAttachments().get(0).getFileUrl();
        String expectedUrlPattern = String.format("https://my-bucket.s3.eu-west-1.amazonaws.com/messages/.*_%s", fileName);
        assertTrue(actualUrl.matches(expectedUrlPattern));

        verify(messageRepo, times(1)).save(any(Message.class));
        verify(attachmentRepo, never()).save(any(Attachment.class));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(messagingTemplate, times(1))
            .convertAndSendToUser(eq("receiver@test.com"), eq("/queue/unread"), any(Integer.class));
    }

    @Test
    void testSendMessageWithInvalidFileType() {
        // Arrange
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        Client sender = new Client();
        sender.setId(senderId);

        Artisan receiver = new Artisan();
        receiver.setId(receiverId);
        receiver.setEmail("receiver@test.com");

        when(artisanRepo.findById(senderId)).thenReturn(Optional.empty());
        when(clientRepo.findById(senderId)).thenReturn(Optional.of(sender));
        when(artisanRepo.findById(receiverId)).thenReturn(Optional.of(receiver));

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/x-executable");
        when(file.getSize()).thenReturn(1024L);

        MessageRequestDTO dto = new MessageRequestDTO();
        dto.setSenderId(senderId);
        dto.setReceiverId(receiverId);
        dto.setContent("Test");
        dto.setFile(file);
        dto.setTempId(UUID.randomUUID().toString());

        // Act
        MessageResponseDTO response = messageService.sendMessage(dto);

        // Assert
        assertEquals(MessageStatus.FAILED, response.getMessageStatus());
        assertTrue(response.getMessageError().contains("Type de fichier non autorisé"));
        verify(messageRepo, never()).save(any(Message.class));
    }

    @Test
    void testSendMessageWithFileTooLarge() {
        // Arrange
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        Client sender = new Client();
        sender.setId(senderId);

        Artisan receiver = new Artisan();
        receiver.setId(receiverId);

        when(artisanRepo.findById(senderId)).thenReturn(Optional.empty());
        when(clientRepo.findById(senderId)).thenReturn(Optional.of(sender));
        when(artisanRepo.findById(receiverId)).thenReturn(Optional.of(receiver));

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(20 * 1024 * 1024L);

        MessageRequestDTO dto = new MessageRequestDTO();
        dto.setSenderId(senderId);
        dto.setReceiverId(receiverId);
        dto.setContent("Test");
        dto.setFile(file);
        dto.setTempId(UUID.randomUUID().toString());

        // Act
        MessageResponseDTO response = messageService.sendMessage(dto);

        // Assert
        assertEquals(MessageStatus.FAILED, response.getMessageStatus());
        assertTrue(response.getMessageError().contains("Fichier trop volumineux"));
        verify(messageRepo, never()).save(any(Message.class));
    }

    @Test
    void testSendMessageWithNonExistentSender() {
        // Arrange
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        when(artisanRepo.findById(senderId)).thenReturn(Optional.empty());
        when(clientRepo.findById(senderId)).thenReturn(Optional.empty());

        MessageRequestDTO dto = new MessageRequestDTO();
        dto.setSenderId(senderId);
        dto.setReceiverId(receiverId);
        dto.setContent("Test");
        dto.setTempId(UUID.randomUUID().toString());

        // Act
        MessageResponseDTO response = messageService.sendMessage(dto);

        // Assert
        assertEquals(MessageStatus.FAILED, response.getMessageStatus());
        assertTrue(response.getMessageError().contains("Utilisateur non trouvé"));
        verify(messageRepo, never()).save(any(Message.class));
    }

    @Test
    void testGetConversation() {
        // Arrange
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();

        Client user1 = new Client();
        user1.setId(user1Id);
        user1.setEmail("user1@test.com");

        Artisan user2 = new Artisan();
        user2.setId(user2Id);
        user2.setEmail("user2@test.com");

        Message msg1 = new Message();
        msg1.setId(UUID.randomUUID());
        msg1.setSender(user1);
        msg1.setReceiver(user2);
        msg1.setContent("Hello");
        msg1.setCreatedAt(LocalDateTime.now().minusHours(2));

        Message msg2 = new Message();
        msg2.setId(UUID.randomUUID());
        msg2.setSender(user2);
        msg2.setReceiver(user1);
        msg2.setContent("Hi there");
        msg2.setCreatedAt(LocalDateTime.now().minusHours(1));

        when(artisanRepo.existsById(user1Id)).thenReturn(false);
        when(clientRepo.existsById(user1Id)).thenReturn(true);
        when(artisanRepo.existsById(user2Id)).thenReturn(true);
        when(clientRepo.existsById(user2Id)).thenReturn(false);

        when(messageRepo.findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAtAsc(
            user1Id, user2Id, user1Id, user2Id))
            .thenReturn(Arrays.asList(msg1, msg2));

        // Act
        List<MessageResponseDTO> conversation = messageService.getConversation(user1Id, user2Id);

        // Assert
        assertNotNull(conversation);
        assertEquals(2, conversation.size());
        assertEquals("Hello", conversation.get(0).getContent());
        assertEquals("Hi there", conversation.get(1).getContent());
    }

    @Test
    void testGetConversationWithInvalidUserId() {
        // Arrange
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();

        when(artisanRepo.existsById(user1Id)).thenReturn(false);
        when(clientRepo.existsById(user1Id)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.getConversation(user1Id, user2Id);
        });
    }

    @Test
    void testGetConversationSummaries() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID otherUserId1 = UUID.randomUUID();
        UUID otherUserId2 = UUID.randomUUID();

        Client currentUser = new Client();
        currentUser.setId(userId);
        currentUser.setEmail("current@test.com");
        currentUser.setFirstName("John");
        currentUser.setLastName("Doe");
        currentUser.setUserRole(UserRole.CLIENT);

        Artisan otherUser1 = new Artisan();
        otherUser1.setId(otherUserId1);
        otherUser1.setName("Artisan 1");
        otherUser1.setEmail("artisan1@test.com");
        otherUser1.setUserRole(UserRole.ARTISAN);

        Artisan otherUser2 = new Artisan();
        otherUser2.setId(otherUserId2);
        otherUser2.setName("Artisan 2");
        otherUser2.setEmail("artisan2@test.com");
        otherUser2.setUserRole(UserRole.ARTISAN);

        Message msg1 = new Message();
        msg1.setId(UUID.randomUUID());
        msg1.setSender(currentUser);
        msg1.setReceiver(otherUser1);
        msg1.setContent("Message to Artisan 1");
        msg1.setCreatedAt(LocalDateTime.now().minusHours(2));
        msg1.setRead(true);

        Message msg2 = new Message();
        msg2.setId(UUID.randomUUID());
        msg2.setSender(otherUser1);
        msg2.setReceiver(currentUser);
        msg2.setContent("Response from Artisan 1");
        msg2.setCreatedAt(LocalDateTime.now().minusHours(1));
        msg2.setRead(false);

        Message msg3 = new Message();
        msg3.setId(UUID.randomUUID());
        msg3.setSender(otherUser2);
        msg3.setReceiver(currentUser);
        msg3.setContent("Message from Artisan 2");
        msg3.setCreatedAt(LocalDateTime.now().minusMinutes(30));
        msg3.setRead(false);

        when(artisanRepo.findById(userId)).thenReturn(Optional.empty());
        when(clientRepo.findById(userId)).thenReturn(Optional.of(currentUser));

        when(messageRepo.findAllBySenderIdOrReceiverId(userId, userId))
            .thenReturn(Arrays.asList(msg1, msg2, msg3));

        when(messageRepo.findByReceiverAndIsReadFalse(currentUser))
            .thenReturn(Arrays.asList(msg2, msg3));

        // Act
        List<ConversationSummaryDTO> summaries = messageService.getConversationSummaries(userId);

        // Assert
        assertNotNull(summaries);
        assertEquals(2, summaries.size());
        
        // Le plus récent devrait être en premier
        assertEquals(otherUserId2, summaries.get(0).getOtherUserId());
        assertEquals("Artisan 2", summaries.get(0).getOtherUserName());
        assertEquals(1L, summaries.get(0).getUnreadCount());

        assertEquals(otherUserId1, summaries.get(1).getOtherUserId());
        assertEquals("Artisan 1", summaries.get(1).getOtherUserName());
        assertEquals(1L, summaries.get(1).getUnreadCount());
    }

    @Test
    void testGetUnreadMessages() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Client user = new Client();
        user.setId(userId);
        user.setEmail("user@test.com");

        Message msg1 = new Message();
        msg1.setId(UUID.randomUUID());
        msg1.setReceiver(user);
        msg1.setRead(false);

        Message msg2 = new Message();
        msg2.setId(UUID.randomUUID());
        msg2.setReceiver(user);
        msg2.setRead(false);

        when(messageRepo.findByReceiverAndIsReadFalse(user))
            .thenReturn(Arrays.asList(msg1, msg2));

        // Act
        List<Message> unreadMessages = messageService.getUnreadMessages(user);

        // Assert
        assertNotNull(unreadMessages);
        assertEquals(2, unreadMessages.size());
        verify(messageRepo, times(1)).findByReceiverAndIsReadFalse(user);
    }

    @Test
    void testMarkMessageAsRead() {
        // Arrange
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Client user = new Client();
        user.setId(userId);
        user.setEmail("user@test.com");

        Message message = new Message();
        message.setId(messageId);
        message.setReceiver(user);
        message.setRead(false);

        when(messageRepo.findById(messageId)).thenReturn(Optional.of(message));
        when(messageRepo.save(any(Message.class))).thenReturn(message);
        when(messageRepo.findByReceiverAndIsReadFalse(user)).thenReturn(Arrays.asList());

        // Act
        messageService.markMessageAsRead(messageId, user);

        // Assert
        assertTrue(message.getRead());
        verify(messageRepo, times(1)).save(message);
        verify(messagingTemplate, times(1))
            .convertAndSendToUser(eq("user@test.com"), eq("/queue/unread"), eq(0));
    }

    @Test
    void testMarkMessageAsReadByWrongUser() {
        // Arrange
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID wrongUserId = UUID.randomUUID();

        Client user = new Client();
        user.setId(userId);

        Client wrongUser = new Client();
        wrongUser.setId(wrongUserId);

        Message message = new Message();
        message.setId(messageId);
        message.setReceiver(user);
        message.setRead(false);

        when(messageRepo.findById(messageId)).thenReturn(Optional.of(message));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.markMessageAsRead(messageId, wrongUser);
        });

        assertFalse(message.getRead());
        verify(messageRepo, never()).save(any(Message.class));
    }

    @Test
    void testMarkMessageAsReadWithNonExistentMessage() {
        // Arrange
        UUID messageId = UUID.randomUUID();
        Client user = new Client();
        user.setId(UUID.randomUUID());

        when(messageRepo.findById(messageId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.markMessageAsRead(messageId, user);
        });

        verify(messageRepo, never()).save(any(Message.class));
    }
}