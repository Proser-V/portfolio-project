package com.atelierlocal.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.atelierlocal.dto.ConversationSummaryDTO;
import com.atelierlocal.dto.MessageRequestDTO;
import com.atelierlocal.dto.MessageResponseDTO;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.Message;
import com.atelierlocal.model.MessageStatus;
import com.atelierlocal.model.UserRole;
import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.ClientRepo;
import com.atelierlocal.service.MessageService;

class MessageControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private MessageService messageService;

    @Mock
    private ArtisanRepo artisanRepo;

    @Mock
    private ClientRepo clientRepo;

    @InjectMocks
    private MessageController messageController;

    private UUID authenticatedUserId;
    private Principal mockPrincipal;
    private Artisan mockUser;
    private String userEmail = "test@mail.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticatedUserId = UUID.randomUUID();
        mockPrincipal = () -> userEmail;

        mockUser = new Artisan();
        mockUser.setId(authenticatedUserId);
        mockUser.setEmail(userEmail);
        mockUser.setUserRole(UserRole.ARTISAN);

        when(artisanRepo.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
    }

    // ==================== Tests WebSocket ====================

    @Test
    void testProcessMessageSuccess() {
        UUID receiverId = UUID.randomUUID();
        String receiverEmail = "receiver@mail.com";
        Client receiverUser = new Client();
        receiverUser.setId(receiverId);
        receiverUser.setEmail(receiverEmail);
        receiverUser.setUserRole(UserRole.CLIENT);

        MessageRequestDTO request = new MessageRequestDTO();
        request.setReceiverId(receiverId);
        request.setContent("Hello");

        MessageResponseDTO response = new MessageResponseDTO("Message sent successfully");
        response.setReceiverId(receiverId);
        response.setSenderId(authenticatedUserId);

        when(artisanRepo.findById(receiverId)).thenReturn(Optional.empty());
        when(clientRepo.findById(receiverId)).thenReturn(Optional.of(receiverUser));
        when(messageService.sendMessage(any(MessageRequestDTO.class))).thenReturn(response);

        messageController.processMessage(request, mockPrincipal);

        ArgumentCaptor<MessageRequestDTO> captor = ArgumentCaptor.forClass(MessageRequestDTO.class);
        verify(messageService, times(1)).sendMessage(captor.capture());
        assertEquals(authenticatedUserId, captor.getValue().getSenderId());
        assertEquals("Hello", captor.getValue().getContent());
        assertNull(captor.getValue().getFile());

        verify(messagingTemplate, times(1))
                .convertAndSendToUser(eq(receiverEmail), eq("/queue/messages"), eq(response));
        verify(messagingTemplate, times(1))
                .convertAndSendToUser(eq(userEmail), eq("/queue/messages"), eq(response));
    }

    @Test
    void testProcessMessageUnauthorizedRole() {
        // Artisan essaie d'envoyer à un autre artisan (non autorisé)
        UUID receiverId = UUID.randomUUID();
        String receiverEmail = "receiver@mail.com";
        
        Artisan receiverUser = new Artisan();
        receiverUser.setId(receiverId);
        receiverUser.setEmail(receiverEmail);
        receiverUser.setUserRole(UserRole.ARTISAN);

        MessageRequestDTO request = new MessageRequestDTO();
        request.setReceiverId(receiverId);
        request.setContent("Hello");

        when(artisanRepo.findById(receiverId)).thenReturn(Optional.of(receiverUser));

        messageController.processMessage(request, mockPrincipal);

        ArgumentCaptor<MessageResponseDTO> captor = ArgumentCaptor.forClass(MessageResponseDTO.class);
        verify(messagingTemplate, times(1))
                .convertAndSendToUser(eq(userEmail), eq("/queue/messages"), captor.capture());

        MessageResponseDTO capturedResponse = captor.getValue();
        assertTrue(capturedResponse.getMessageError().contains("ne peuvent contacter"));

        verify(messageService, never()).sendMessage(any());
    }

    @Test
    void testProcessMessageReceiverNotFound() {
        UUID receiverId = UUID.randomUUID();

        MessageRequestDTO request = new MessageRequestDTO();
        request.setReceiverId(receiverId);
        request.setContent("Hello");

        when(artisanRepo.findById(receiverId)).thenReturn(Optional.empty());
        when(clientRepo.findById(receiverId)).thenReturn(Optional.empty());

        messageController.processMessage(request, mockPrincipal);

        ArgumentCaptor<MessageResponseDTO> captor = ArgumentCaptor.forClass(MessageResponseDTO.class);
        verify(messagingTemplate, times(1))
                .convertAndSendToUser(eq(userEmail), eq("/queue/messages"), captor.capture());

        MessageResponseDTO capturedResponse = captor.getValue();
        assertTrue(capturedResponse.getMessageError().contains("Destinataire non trouvé"));

        verify(messageService, never()).sendMessage(any());
    }

    @Test
    void testProcessMessageException() {
        UUID receiverId = UUID.randomUUID();
        String receiverEmail = "receiver@mail.com";

        Client receiverUser = new Client();
        receiverUser.setId(receiverId);
        receiverUser.setEmail(receiverEmail);
        receiverUser.setUserRole(UserRole.CLIENT);

        MessageRequestDTO request = new MessageRequestDTO();
        request.setReceiverId(receiverId);
        request.setContent("Hello");

        when(artisanRepo.findById(receiverId)).thenReturn(Optional.empty());
        when(clientRepo.findById(receiverId)).thenReturn(Optional.of(receiverUser));
        when(messageService.sendMessage(any(MessageRequestDTO.class)))
                .thenThrow(new RuntimeException("Service down"));

        messageController.processMessage(request, mockPrincipal);

        ArgumentCaptor<MessageResponseDTO> captor = ArgumentCaptor.forClass(MessageResponseDTO.class);
        verify(messagingTemplate, times(1))
                .convertAndSendToUser(eq(userEmail), eq("/queue/messages"), captor.capture());

        MessageResponseDTO capturedResponse = captor.getValue();
        assertTrue(capturedResponse.getMessageError().contains("Service down"));

        verify(messagingTemplate, never())
                .convertAndSendToUser(eq(receiverEmail), eq("/queue/messages"), any(MessageResponseDTO.class));
    }

    // ==================== Tests REST avec fichiers ====================

    @Test
    void testSendMessageWithAttachmentSuccess() {
        UUID receiverId = UUID.randomUUID();
        String receiverEmail = "receiver@mail.com";
        
        Client receiverUser = new Client();
        receiverUser.setId(receiverId);
        receiverUser.setEmail(receiverEmail);
        receiverUser.setUserRole(UserRole.CLIENT);

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.png");

        Message savedMessage = new Message();
        savedMessage.setId(UUID.randomUUID());
        savedMessage.setContent("Test message");
        savedMessage.setSender(mockUser);
        savedMessage.setReceiver(receiverUser);
        savedMessage.setMessageStatus(MessageStatus.SENT);

        MessageResponseDTO response = new MessageResponseDTO(savedMessage);

        when(artisanRepo.findById(receiverId)).thenReturn(Optional.empty());
        when(clientRepo.findById(receiverId)).thenReturn(Optional.of(receiverUser));
        when(messageService.sendMessage(any(MessageRequestDTO.class))).thenReturn(response);

        ResponseEntity<MessageResponseDTO> responseEntity = messageController.sendMessageWithAttachment(
            receiverId, "Test message", mockFile, mockPrincipal
        );

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Test message", responseEntity.getBody().getContent());
        assertEquals(MessageStatus.SENT, responseEntity.getBody().getMessageStatus());

        ArgumentCaptor<MessageRequestDTO> captor = ArgumentCaptor.forClass(MessageRequestDTO.class);
        verify(messageService, times(1)).sendMessage(captor.capture());
        
        MessageRequestDTO capturedDTO = captor.getValue();
        assertEquals(authenticatedUserId, capturedDTO.getSenderId());
        assertEquals(receiverId, capturedDTO.getReceiverId());
        assertEquals("Test message", capturedDTO.getContent());
        assertEquals(mockFile, capturedDTO.getFile());

        verify(messagingTemplate, times(2)).convertAndSendToUser(any(), eq("/queue/messages"), eq(response));
    }

    @Test
    void testSendMessageWithAttachmentUnauthorizedRole() {
        // Client essaie d'envoyer à un autre client (non autorisé)
        UUID senderId = UUID.randomUUID();
        Client senderUser = new Client();
        senderUser.setId(senderId);
        senderUser.setEmail(userEmail);
        senderUser.setUserRole(UserRole.CLIENT);

        UUID receiverId = UUID.randomUUID();
        Client receiverUser = new Client();
        receiverUser.setId(receiverId);
        receiverUser.setEmail("receiver@mail.com");
        receiverUser.setUserRole(UserRole.CLIENT);

        when(artisanRepo.findByEmail(userEmail)).thenReturn(Optional.empty());
        when(clientRepo.findByEmail(userEmail)).thenReturn(Optional.of(senderUser));
        when(artisanRepo.findById(receiverId)).thenReturn(Optional.empty());
        when(clientRepo.findById(receiverId)).thenReturn(Optional.of(receiverUser));

        ResponseEntity<MessageResponseDTO> responseEntity = messageController.sendMessageWithAttachment(
            receiverId, "Test", null, mockPrincipal
        );

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().getMessageError().contains("ne peuvent contacter"));

        verify(messageService, never()).sendMessage(any());
    }

    @Test
    void testSendMessageWithAttachmentReceiverNotFound() {
        UUID receiverId = UUID.randomUUID();

        when(artisanRepo.findById(receiverId)).thenReturn(Optional.empty());
        when(clientRepo.findById(receiverId)).thenReturn(Optional.empty());

        ResponseEntity<MessageResponseDTO> responseEntity = messageController.sendMessageWithAttachment(
            receiverId, "Test", null, mockPrincipal
        );

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().getMessageError().contains("Destinataire non trouvé"));

        verify(messageService, never()).sendMessage(any());
    }

    @Test
    void testSendMessageWithAttachmentServiceException() {
        UUID receiverId = UUID.randomUUID();
        Client receiverUser = new Client();
        receiverUser.setId(receiverId);
        receiverUser.setEmail("receiver@mail.com");
        receiverUser.setUserRole(UserRole.CLIENT);

        when(artisanRepo.findById(receiverId)).thenReturn(Optional.empty());
        when(clientRepo.findById(receiverId)).thenReturn(Optional.of(receiverUser));
        when(messageService.sendMessage(any())).thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<MessageResponseDTO> responseEntity = messageController.sendMessageWithAttachment(
            receiverId, "Test", null, mockPrincipal
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().getMessageError().contains("DB Error"));
    }

    // ==================== Tests GET /history ====================

    @Test
    void testGetHistorySuccess() {
        UUID otherUserId = UUID.randomUUID();

        MessageResponseDTO msg1 = new MessageResponseDTO("hello");
        MessageResponseDTO msg2 = new MessageResponseDTO("world");

        when(messageService.getConversation(authenticatedUserId, otherUserId))
                .thenReturn(Arrays.asList(msg1, msg2));

        ResponseEntity<List<MessageResponseDTO>> responseEntity = 
            messageController.getHistory(authenticatedUserId, otherUserId, mockPrincipal);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<MessageResponseDTO> history = responseEntity.getBody();
        assertNotNull(history);
        assertEquals(2, history.size());

        verify(messageService, times(1)).getConversation(authenticatedUserId, otherUserId);
    }

    @Test
    void testGetHistoryUnauthorized() {
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        ResponseEntity<List<MessageResponseDTO>> responseEntity = 
            messageController.getHistory(user1, user2, mockPrincipal);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());

        verify(messageService, never()).getConversation(any(), any());
    }

    @Test
    void testGetHistoryException() {
        UUID otherUserId = UUID.randomUUID();

        when(messageService.getConversation(authenticatedUserId, otherUserId))
                .thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<List<MessageResponseDTO>> responseEntity = 
            messageController.getHistory(authenticatedUserId, otherUserId, mockPrincipal);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    // ==================== Tests GET /conversations/{userId} ====================

    @Test
    void testGetConversationsSuccess() {
        ConversationSummaryDTO summary1 = new ConversationSummaryDTO(
            UUID.randomUUID(), "User 1", "ARTISAN", null, 
            "Last message", LocalDateTime.now(), 2L
        );
        ConversationSummaryDTO summary2 = new ConversationSummaryDTO(
            UUID.randomUUID(), "User 2", "CLIENT", null, 
            "Another message", LocalDateTime.now().minusHours(1), 0L
        );

        when(messageService.getConversationSummaries(authenticatedUserId))
                .thenReturn(Arrays.asList(summary1, summary2));

        ResponseEntity<List<ConversationSummaryDTO>> responseEntity = 
            messageController.getConversations(authenticatedUserId, mockPrincipal);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ConversationSummaryDTO> conversations = responseEntity.getBody();
        assertNotNull(conversations);
        assertEquals(2, conversations.size());
        assertEquals("User 1", conversations.get(0).getOtherUserName());
        assertEquals(2L, conversations.get(0).getUnreadCount());

        verify(messageService, times(1)).getConversationSummaries(authenticatedUserId);
    }

    @Test
    void testGetConversationsUnauthorized() {
        UUID otherUserId = UUID.randomUUID();

        ResponseEntity<List<ConversationSummaryDTO>> responseEntity = 
            messageController.getConversations(otherUserId, mockPrincipal);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());

        verify(messageService, never()).getConversationSummaries(any());
    }

    @Test
    void testGetConversationsIllegalArgumentException() {
        when(messageService.getConversationSummaries(authenticatedUserId))
                .thenThrow(new IllegalArgumentException("Invalid user"));

        ResponseEntity<List<ConversationSummaryDTO>> responseEntity = 
            messageController.getConversations(authenticatedUserId, mockPrincipal);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testGetConversationsGeneralException() {
        when(messageService.getConversationSummaries(authenticatedUserId))
                .thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<List<ConversationSummaryDTO>> responseEntity = 
            messageController.getConversations(authenticatedUserId, mockPrincipal);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    // ==================== Tests GET /unread ====================

    @Test
    void testGetUnreadMessagesSuccess() {
        Client sender = new Client();
        sender.setId(UUID.randomUUID());
        sender.setEmail("sender@test.com");

        Message msg1 = new Message();
        msg1.setId(UUID.randomUUID());
        msg1.setContent("Unread 1");
        msg1.setRead(false);
        msg1.setSender(sender);
        msg1.setReceiver(mockUser);

        Message msg2 = new Message();
        msg2.setId(UUID.randomUUID());
        msg2.setContent("Unread 2");
        msg2.setRead(false);
        msg2.setSender(sender);
        msg2.setReceiver(mockUser);

        when(messageService.getUnreadMessages(mockUser))
                .thenReturn(Arrays.asList(msg1, msg2));

        ResponseEntity<List<MessageResponseDTO>> responseEntity = 
            messageController.getUnreadMessages(mockPrincipal);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<MessageResponseDTO> unreadMessages = responseEntity.getBody();
        assertNotNull(unreadMessages);
        assertEquals(2, unreadMessages.size());

        verify(messageService, times(1)).getUnreadMessages(mockUser);
    }

    @Test
    void testGetUnreadMessagesIllegalArgumentException() {
        when(messageService.getUnreadMessages(mockUser))
                .thenThrow(new IllegalArgumentException("Invalid user"));

        ResponseEntity<List<MessageResponseDTO>> responseEntity = 
            messageController.getUnreadMessages(mockPrincipal);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testGetUnreadMessagesGeneralException() {
        when(messageService.getUnreadMessages(mockUser))
                .thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<List<MessageResponseDTO>> responseEntity = 
            messageController.getUnreadMessages(mockPrincipal);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    // ==================== Tests POST /{messageId}/read ====================

    @Test
    void testMarkMessageAsReadSuccess() {
        UUID messageId = UUID.randomUUID();

        doNothing().when(messageService).markMessageAsRead(messageId, mockUser);

        ResponseEntity<Void> responseEntity = 
            messageController.markMessageAsRead(messageId, mockPrincipal);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(messageService, times(1)).markMessageAsRead(messageId, mockUser);
    }

    @Test
    void testMarkMessageAsReadIllegalArgumentException() {
        UUID messageId = UUID.randomUUID();

        doThrow(new IllegalArgumentException("Not authorized"))
                .when(messageService).markMessageAsRead(messageId, mockUser);

        ResponseEntity<Void> responseEntity = 
            messageController.markMessageAsRead(messageId, mockPrincipal);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testMarkMessageAsReadGeneralException() {
        UUID messageId = UUID.randomUUID();

        doThrow(new RuntimeException("DB Error"))
                .when(messageService).markMessageAsRead(messageId, mockUser);

        ResponseEntity<Void> responseEntity = 
            messageController.markMessageAsRead(messageId, mockPrincipal);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    // ==================== Tests d'autorisation par rôle ====================

    @Test
    void testClientCanSendToArtisan() {
        UUID clientId = UUID.randomUUID();
        UUID artisanId = UUID.randomUUID();
        
        Client client = new Client();
        client.setId(clientId);
        client.setEmail("client@mail.com");
        client.setUserRole(UserRole.CLIENT);

        Artisan artisan = new Artisan();
        artisan.setId(artisanId);
        artisan.setEmail("artisan@mail.com");
        artisan.setUserRole(UserRole.ARTISAN);

        when(artisanRepo.findByEmail("client@mail.com")).thenReturn(Optional.empty());
        when(clientRepo.findByEmail("client@mail.com")).thenReturn(Optional.of(client));
        when(artisanRepo.findById(artisanId)).thenReturn(Optional.of(artisan));

        MessageResponseDTO response = new MessageResponseDTO("Success");
        when(messageService.sendMessage(any())).thenReturn(response);

        Principal clientPrincipal = () -> "client@mail.com";
        
        ResponseEntity<MessageResponseDTO> responseEntity = messageController.sendMessageWithAttachment(
            artisanId, "Hello", null, clientPrincipal
        );

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(messageService, times(1)).sendMessage(any());
    }

    @Test
    void testArtisanCanSendToClient() {
        // Déjà testé dans testSendMessageWithAttachmentSuccess mais pour clarté
        UUID receiverId = UUID.randomUUID();
        Client receiverUser = new Client();
        receiverUser.setId(receiverId);
        receiverUser.setEmail("client@mail.com");
        receiverUser.setUserRole(UserRole.CLIENT);

        when(artisanRepo.findById(receiverId)).thenReturn(Optional.empty());
        when(clientRepo.findById(receiverId)).thenReturn(Optional.of(receiverUser));

        MessageResponseDTO response = new MessageResponseDTO("Success");
        when(messageService.sendMessage(any())).thenReturn(response);

        ResponseEntity<MessageResponseDTO> responseEntity = messageController.sendMessageWithAttachment(
            receiverId, "Hello", null, mockPrincipal
        );

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(messageService, times(1)).sendMessage(any());
    }
}