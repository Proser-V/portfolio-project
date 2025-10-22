package com.atelierlocal.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.atelierlocal.dto.MessageRequestDTO;
import com.atelierlocal.dto.MessageResponseDTO;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.UserRole;
import com.atelierlocal.repository.UserRepo;
import com.atelierlocal.service.MessageService;

class MessageControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private MessageService messageService;

    @Mock
    private UserRepo userRepo;

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

        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
    }

    @Test
    void testProcessMessageSuccess() {
        UUID receiverId = UUID.randomUUID();
        String receiverEmail = "receiver@mail.com";
        Artisan receiverUser = new Artisan();
        receiverUser.setId(receiverId);
        receiverUser.setEmail(receiverEmail);

        MessageRequestDTO request = new MessageRequestDTO();
        request.setReceiverId(receiverId);
        request.setContent("Hello");

        MessageResponseDTO response = new MessageResponseDTO("Hello");
        response.setReceiverId(receiverId);
        response.setSenderId(authenticatedUserId);

        when(userRepo.findById(receiverId)).thenReturn(Optional.of(receiverUser));
        when(messageService.sendMessage(any(MessageRequestDTO.class))).thenReturn(response);

        messageController.processMessage(request, mockPrincipal);

        ArgumentCaptor<MessageRequestDTO> captor = ArgumentCaptor.forClass(MessageRequestDTO.class);
        verify(messageService, times(1)).sendMessage(captor.capture());
        assertEquals(authenticatedUserId, captor.getValue().getSenderId());

        verify(messagingTemplate, times(1))
            .convertAndSendToUser(eq(receiverEmail), eq("/queue/messages"), eq(response));
        verify(messagingTemplate, times(1))
            .convertAndSendToUser(eq(userEmail), eq("/queue/messages"), eq(response));
    }

    @Test
    void testProcessMessageException() {
        UUID receiverId = UUID.randomUUID();
        String receiverEmail = "receiver@mail.com";
        String senderEmail = "sender@mail.com";

        Artisan senderUser = new Artisan();
        senderUser.setId(UUID.randomUUID());
        senderUser.setEmail(senderEmail);
        senderUser.setUserRole(UserRole.ARTISAN);
        when(userRepo.findByEmail(senderEmail)).thenReturn(Optional.of(senderUser));

        Artisan receiverUser = new Artisan();
        receiverUser.setId(receiverId);
        receiverUser.setEmail(receiverEmail);
        receiverUser.setUserRole(UserRole.CLIENT);
        when(userRepo.findById(receiverId)).thenReturn(Optional.of(receiverUser));

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(senderEmail);

        MessageRequestDTO request = new MessageRequestDTO();
        request.setReceiverId(receiverId);
        request.setContent("Hello");

        when(messageService.sendMessage(any(MessageRequestDTO.class)))
            .thenThrow(new RuntimeException("Service down"));

        messageController.processMessage(request, principal);

        ArgumentCaptor<MessageResponseDTO> captor = ArgumentCaptor.forClass(MessageResponseDTO.class);
        verify(messagingTemplate, times(1))
            .convertAndSendToUser(eq(senderEmail), eq("/queue/messages"), captor.capture());

        MessageResponseDTO capturedResponse = captor.getValue();
        assertTrue(capturedResponse.getMessageError().contains("Service down"));

        verify(messagingTemplate, never())
            .convertAndSendToUser(eq(receiverEmail), eq("/queue/messages"), any(MessageResponseDTO.class));
    }

    @Test
    void testGetHistorySuccess() {
        UUID otherUserId = UUID.randomUUID();

        MessageResponseDTO msg1 = new MessageResponseDTO("hello");
        MessageResponseDTO msg2 = new MessageResponseDTO("world");

        when(messageService.getConversation(authenticatedUserId, otherUserId))
            .thenReturn(List.of(msg1, msg2));

        var responseEntity = messageController.getHistory(authenticatedUserId, otherUserId, mockPrincipal);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<MessageResponseDTO> history = responseEntity.getBody();
        assertNotNull(history);
        assertEquals(2, history.size());
        assertEquals("hello", history.get(0).getMessageError());
        assertEquals("world", history.get(1).getMessageError());

        verify(messageService, times(1)).getConversation(authenticatedUserId, otherUserId);
    }

    @Test
    void testGetHistoryUnauthorized() {
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        var responseEntity = messageController.getHistory(user1, user2, mockPrincipal);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());

        verify(messageService, never()).getConversation(any(), any());
    }
}