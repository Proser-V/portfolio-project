package com.atelierlocal.service;

import com.atelierlocal.dto.MessageRequestDTO;
import com.atelierlocal.dto.MessageResponseDTO;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.S3Properties;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Attachment;
import com.atelierlocal.repository.MessageRepo;
import com.atelierlocal.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.*;
import static org.mockito.ArgumentMatchers.any;

import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    @Mock
    private MessageRepo messageRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private AttachmentService attachmentService;

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Properties s3Properties;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendMessageWithoutFile() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        String expectedContent = "Hello Artisan";

        Client sender = new Client();
        sender.setId(senderId);

        Artisan receiver = new Artisan();
        receiver.setId(receiverId);

        when(userRepo.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepo.findById(receiverId)).thenReturn(Optional.of(receiver));

        com.atelierlocal.model.Message savedMessage = new com.atelierlocal.model.Message();
        savedMessage.setContent(expectedContent);
        savedMessage.setSender(sender);
        savedMessage.setReceiver(receiver);
        savedMessage.setTimestamp(LocalDateTime.now());
        savedMessage.setMessageStatus(com.atelierlocal.model.MessageStatus.DELIVERED);
        when(messageRepo.save(any(com.atelierlocal.model.Message.class))).thenReturn(savedMessage);

        MessageRequestDTO dto = new MessageRequestDTO();
        dto.setSenderId(senderId);
        dto.setReceiverId(receiverId);
        dto.setContent(expectedContent);
        dto.setFile(null);

        MessageResponseDTO response = messageService.sendMessage(dto);

        assertNotNull(response, "Response should not be null");
        assertEquals(expectedContent, response.getContent(), "Message content should match");
        verify(messageRepo, times(1)).save(any(com.atelierlocal.model.Message.class));
        verify(attachmentService, never()).linkToMessage(any(), any());
    }

    @Test
    void testSendMessageWithFile() throws Exception {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        String expectedContent = "Message with file";

        Client sender = new Client();
        sender.setId(senderId);

        Artisan receiver = new Artisan();
        receiver.setId(receiverId);

        when(userRepo.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepo.findById(receiverId)).thenReturn(Optional.of(receiver));

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(1024L);
        when(file.getOriginalFilename()).thenReturn("test.png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));

        when(s3Properties.getBucketName()).thenReturn("my-bucket");
        when(s3Properties.getRegion()).thenReturn("eu-west-1");

        com.atelierlocal.model.Message savedMessage = new com.atelierlocal.model.Message();
        savedMessage.setContent(expectedContent);
        savedMessage.setSender(sender);
        savedMessage.setReceiver(receiver);
        savedMessage.setTimestamp(LocalDateTime.now());
        savedMessage.setMessageStatus(com.atelierlocal.model.MessageStatus.DELIVERED);
        when(messageRepo.save(any(com.atelierlocal.model.Message.class))).thenReturn(savedMessage);

        doNothing().when(attachmentService).linkToMessage(any(Attachment.class), any(com.atelierlocal.model.Message.class));

        MessageRequestDTO dto = new MessageRequestDTO();
        dto.setSenderId(senderId);
        dto.setReceiverId(receiverId);
        dto.setContent(expectedContent);
        dto.setFile(file);

        MessageResponseDTO response = messageService.sendMessage(dto);

        assertNotNull(response, "Response should not be null");
        assertEquals(expectedContent, response.getContent(), "Message content should match");
        verify(messageRepo, times(1)).save(any(com.atelierlocal.model.Message.class));
        verify(attachmentService, times(1)).linkToMessage(any(Attachment.class), any(com.atelierlocal.model.Message.class));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}
