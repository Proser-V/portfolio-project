package com.atelierlocal.service;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.atelierlocal.dto.MessageRequestDTO;
import com.atelierlocal.dto.MessageResponseDTO;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Attachment;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.Message;
import com.atelierlocal.model.MessageStatus;
import com.atelierlocal.model.S3Properties;
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

        // Mock user lookup
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
        assertNotNull(response, "Response should not be null");
        assertEquals(expectedContent, response.getContent(), "Message content should match");
        assertEquals(MessageStatus.SENT, response.getMessageStatus(), "Message status should match");
        
        verify(messageRepo, times(1)).save(any(Message.class));
        verify(attachmentRepo, never()).save(any(Attachment.class));
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        
        // Verify unread notification
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

        // Mock user lookup
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

        // Simuler l'attachment qui sera créé lors de la sauvegarde (cascade)
        Attachment attachment = new Attachment();
        attachment.setFileUrl(String.format("https://my-bucket.s3.eu-west-1.amazonaws.com/messages/%s_%s", 
            UUID.randomUUID(), fileName));
        attachment.setFileType("image/png");
        attachment.setMessage(savedMessage);
        savedMessage.getAttachments().add(attachment);

        // Mock message save (qui déclenche la cascade pour l'attachment)
        when(messageRepo.save(any(Message.class))).thenReturn(savedMessage);

        MessageRequestDTO dto = new MessageRequestDTO();
        dto.setSenderId(senderId);
        dto.setReceiverId(receiverId);
        dto.setContent(expectedContent);
        dto.setFile(file);

        // Act
        MessageResponseDTO response = messageService.sendMessage(dto);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(expectedContent, response.getContent(), "Message content should match");
        assertEquals(1, response.getAttachments().size(), "Should have one attachment");

        String actualUrl = response.getAttachments().get(0).getFileUrl();
        String expectedUrlPattern = String.format("https://my-bucket.s3.eu-west-1.amazonaws.com/messages/.*_%s", fileName);
        assertTrue(actualUrl.matches(expectedUrlPattern), 
            "Attachment URL should match pattern: " + expectedUrlPattern + ", but was: " + actualUrl);

        verify(messageRepo, times(1)).save(any(Message.class));
        verify(attachmentRepo, never()).save(any(Attachment.class));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        verify(messagingTemplate, times(1))
            .convertAndSendToUser(eq("receiver@test.com"), eq("/queue/unread"), any(Integer.class));
    }
}