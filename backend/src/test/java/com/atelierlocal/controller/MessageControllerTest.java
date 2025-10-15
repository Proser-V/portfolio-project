package com.atelierlocal.controller;

import com.atelierlocal.dto.MessageRequestDTO;
import com.atelierlocal.dto.MessageResponseDTO;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.repository.UserRepo;
import com.atelierlocal.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticatedUserId = UUID.randomUUID();
        mockPrincipal = () -> "test@mail.com";

        mockUser = new Artisan();
        mockUser.setId(authenticatedUserId);
        mockUser.setEmail("test@mail.com");

        when(userRepo.findByEmail("test@mail.com")).thenReturn(Optional.of(mockUser));
    }

    @Test
    void testProcessMessageSuccess() {
        UUID receiverId = UUID.randomUUID();

        MessageRequestDTO request = new MessageRequestDTO();
        request.setReceiverId(receiverId);
        request.setContent("Hello");

        MessageResponseDTO response = new MessageResponseDTO("Hello");
        response.setReceiverId(receiverId);
        when(messageService.sendMessage(any(MessageRequestDTO.class))).thenReturn(response);

        messageController.processMessage(request, mockPrincipal);

        ArgumentCaptor<MessageRequestDTO> captor = ArgumentCaptor.forClass(MessageRequestDTO.class);
        verify(messageService, times(1)).sendMessage(captor.capture());
        assertEquals(authenticatedUserId, captor.getValue().getSenderId());

        verify(messagingTemplate, times(1))
            .convertAndSendToUser(eq(receiverId.toString()), eq("/queue/messages"), eq(response));
    }

    @Test
    void testProcessMessageException() {
        UUID receiverId = UUID.randomUUID();

        MessageRequestDTO request = new MessageRequestDTO();
        request.setReceiverId(receiverId);
        request.setContent("Hello");

        when(messageService.sendMessage(any(MessageRequestDTO.class)))
            .thenThrow(new RuntimeException("Service down"));

        messageController.processMessage(request, mockPrincipal);

        verify(messageService, times(1)).sendMessage(any(MessageRequestDTO.class));

        ArgumentCaptor<MessageResponseDTO> captor = ArgumentCaptor.forClass(MessageResponseDTO.class);
        verify(messagingTemplate, times(2))
            .convertAndSendToUser(anyString(), eq("/queue/messages"), captor.capture());

        for (MessageResponseDTO errorMsg : captor.getAllValues()) {
            assertTrue(errorMsg.getMessageError().contains("Service down"));
        }
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

        assertEquals(HttpStatus.FORBIDDEN , responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());

        verify(messageService, never()).getConversation(any(), any());
    }
}
