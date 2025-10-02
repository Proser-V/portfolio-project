package com.atelierlocal.service;

import com.atelierlocal.dto.MessageRequestDTO;
import com.atelierlocal.dto.MessageResponseDTO;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.S3Properties;
import com.atelierlocal.model.Artisan;
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

        Client sender = new Client();
        sender.setId(senderId);
        sender.setFirstName("John");
        sender.setLastName("Doe");

        Artisan receiver = new Artisan();
        receiver.setId(receiverId);
        receiver.setName("Artisan Bob");

        when(userRepo.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepo.findById(receiverId)).thenReturn(Optional.of(receiver));

        MessageRequestDTO dto = new MessageRequestDTO();
        dto.setSenderId(senderId);
        dto.setReceiverId(receiverId);
        dto.setContent("Hello Artisan");
        dto.setFile(null);

        MessageResponseDTO response = messageService.sendMessage(dto);

        assertNotNull(response);
        assertEquals("Hello Artisan", response.getContent());
        verify(messageRepo, times(1)).save(any());
        verify(attachmentService, never()).linkToMessage(any(), any());
    }

    @Test
    void testSendMessageWithFile() throws Exception {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

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
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1,2,3}));

        when(s3Properties.getBucketName()).thenReturn("my-bucket");
        when(s3Properties.getRegion()).thenReturn("eu-west-1");

        MessageRequestDTO dto = new MessageRequestDTO();
        dto.setSenderId(senderId);
        dto.setReceiverId(receiverId);
        dto.setContent("Message with file");
        dto.setFile(file);

        MessageResponseDTO response = messageService.sendMessage(dto);

        assertNotNull(response);
        assertEquals("Message with file", response.getContent());
        verify(messageRepo, times(1)).save(any());
        verify(attachmentService, times(1)).linkToMessage(any(), any());
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), ArgumentMatchers.<RequestBody>any());
    }
}
