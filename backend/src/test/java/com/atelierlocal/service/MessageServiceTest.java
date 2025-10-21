package com.atelierlocal.service;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.atelierlocal.dto.MessageRequestDTO;
import com.atelierlocal.dto.MessageResponseDTO;
import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.Attachment;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.S3Properties;
import com.atelierlocal.repository.AttachmentRepo;
import com.atelierlocal.repository.MessageRepo;
import com.atelierlocal.repository.UserRepo;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

class MessageServiceTest {

    @Mock
    private MessageRepo messageRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private AttachmentRepo attachmentRepo;

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
        savedMessage.setMessageStatus(com.atelierlocal.model.MessageStatus.DELIVERED);
        when(messageRepo.save(any(com.atelierlocal.model.Message.class))).thenReturn(savedMessage);

        // Configurer le mock pour attachmentRepo.save
        Attachment savedAttachment = new Attachment();
        savedAttachment.setFileUrl("https://my-bucket.s3.eu-west-1.amazonaws.com/messages/" + UUID.randomUUID() + "_test.png");
        savedAttachment.setFileType("image/png");
        when(attachmentRepo.save(any(Attachment.class))).thenReturn(savedAttachment);

        MessageRequestDTO dto = new MessageRequestDTO();
        dto.setSenderId(senderId);
        dto.setReceiverId(receiverId);
        dto.setContent(expectedContent);
        dto.setFile(file);

        // Vérifier que le fichier est bien défini
        assertNotNull(dto.getFile(), "File should not be null");
        assertFalse(dto.getFile().isEmpty(), "File should not be empty");

        MessageResponseDTO response = messageService.sendMessage(dto);

        assertNotNull(response, "Response should not be null");
        assertEquals(expectedContent, response.getContent(), "Message content should match");
        verify(messageRepo, times(1)).save(any(com.atelierlocal.model.Message.class));
        verify(attachmentRepo, times(1)).save(any(Attachment.class)); // Vérifier l'appel à attachmentRepo.save
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}
