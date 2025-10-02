package com.atelierlocal.controller;

import com.atelierlocal.dto.MessageRequestDTO;
import com.atelierlocal.dto.MessageResponseDTO;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.Message;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
void testProcessMessageSuccess() {
    UUID senderId = UUID.randomUUID();
    UUID receiverId = UUID.randomUUID();

    Message sender = mock(Message.class);
    Client senderUser = new Client();
    senderUser.setId(senderId);
    Artisan receiverUser = new Artisan();
    receiverUser.setId(receiverId);

    Message message = mock(Message.class);
    when(message.getId()).thenReturn(UUID.randomUUID());
    when(message.getSender()).thenReturn(senderUser);
    when(message.getReceiver()).thenReturn(receiverUser);
    when(message.getContent()).thenReturn("Hello");
    when(message.getAttachments()).thenReturn(null);
    when(message.getMessageError()).thenReturn(null);
    when(message.getMessageStatus()).thenReturn(null);

    MessageResponseDTO response = new MessageResponseDTO(message);
    when(messageService.sendMessage(any(MessageRequestDTO.class))).thenReturn(response);

    MessageRequestDTO request = new MessageRequestDTO();
    request.setSenderId(senderId);
    request.setReceiverId(receiverId);
    request.setContent("Hello");

    messageController.processMessage(request);

    verify(messageService, times(1)).sendMessage(request);
    verify(messagingTemplate, times(1))
        .convertAndSendToUser(eq(receiverId.toString()), eq("/queue/messages"), eq(response));
}


    @Test
    void testProcessMessageException() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        Client senderUser = new Client();
        senderUser.setId(senderId);
        Artisan receiverUser = new Artisan();
        receiverUser.setId(receiverId);

        Message emptyMessage = mock(Message.class);
        when(emptyMessage.getId()).thenReturn(UUID.randomUUID());
        when(emptyMessage.getSender()).thenReturn(senderUser);
        when(emptyMessage.getReceiver()).thenReturn(receiverUser);
        when(emptyMessage.getContent()).thenReturn(null);
        when(emptyMessage.getAttachments()).thenReturn(null);
        when(emptyMessage.getMessageError()).thenReturn(null);
        when(emptyMessage.getMessageStatus()).thenReturn(null);

        when(messageService.sendMessage(any(MessageRequestDTO.class)))
            .thenThrow(new RuntimeException("Service down"));

        MessageRequestDTO request = new MessageRequestDTO();
        request.setSenderId(senderId);
        request.setReceiverId(receiverId);
        request.setContent("Hello");

        messageController.processMessage(request);

        verify(messageService, times(1)).sendMessage(request);

        ArgumentCaptor<MessageResponseDTO> captor = ArgumentCaptor.forClass(MessageResponseDTO.class);
        verify(messagingTemplate, times(2))
            .convertAndSendToUser(anyString(), eq("/queue/messages"), captor.capture());

        for (MessageResponseDTO errorMsg : captor.getAllValues()) {
        assertTrue(errorMsg.getMessageError().contains("Service down"));
        }
    }
}
