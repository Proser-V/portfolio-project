package com.atelierlocal.controller;

import com.atelierlocal.dto.MessageRequestDTO;
import com.atelierlocal.dto.MessageResponseDTO;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Message;
import com.atelierlocal.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MessageControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    private UUID authenticatedUserId;
    private Principal mockPrincipal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticatedUserId = UUID.randomUUID();
        mockPrincipal = () -> authenticatedUserId.toString();
    }

    @Test
    void testProcessMessageSuccess() {
        UUID receiverId = UUID.randomUUID();

        Client senderUser = new Client();
        senderUser.setId(authenticatedUserId);
        Artisan receiverUser = new Artisan();
        receiverUser.setId(receiverId);

        Message message = mock(Message.class);
        when(message.getId()).thenReturn(UUID.randomUUID());
        when(message.getSender()).thenReturn(senderUser);
        when(message.getReceiver()).thenReturn(receiverUser);
        when(message.getContent()).thenReturn("Hello");

        MessageResponseDTO response = new MessageResponseDTO(message);
        when(messageService.sendMessage(any(MessageRequestDTO.class))).thenReturn(response);

        MessageRequestDTO request = new MessageRequestDTO();
        request.setReceiverId(receiverId);
        request.setContent("Hello");

        messageController.processMessage(request, mockPrincipal);

        ArgumentCaptor<MessageRequestDTO> dtoCaptor = ArgumentCaptor.forClass(MessageRequestDTO.class);
        verify(messageService, times(1)).sendMessage(dtoCaptor.capture());
        assertEquals(authenticatedUserId, dtoCaptor.getValue().getSenderId());

        verify(messagingTemplate, times(1))
            .convertAndSendToUser(eq(receiverId.toString()), eq("/queue/messages"), eq(response));
    }

    @Test
    void testProcessMessageException() {
        UUID receiverId = UUID.randomUUID();

        when(messageService.sendMessage(any(MessageRequestDTO.class)))
            .thenThrow(new RuntimeException("Service down"));

        MessageRequestDTO request = new MessageRequestDTO();
        request.setReceiverId(receiverId);
        request.setContent("Hello");

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

        List<MessageResponseDTO> history = messageController.getHistory(authenticatedUserId, otherUserId, mockPrincipal);

        assertEquals(2, history.size());
        assertEquals("hello", history.get(0).getMessageError());
    }

    @Test
    void testGetHistoryUnauthorized() {
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        assertThrows(SecurityException.class, () ->
            messageController.getHistory(user1, user2, mockPrincipal)
        );

        verify(messageService, never()).getConversation(any(), any());
    }
}
