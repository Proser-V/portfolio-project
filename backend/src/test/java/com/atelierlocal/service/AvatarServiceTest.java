package com.atelierlocal.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ServiceClientConfiguration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

class AvatarServiceTest {

    private S3Client s3Client;
    private AvatarService avatarService;

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        avatarService = new AvatarService(s3Client);
    }

    @Test
    void testGetFileExtension() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("image.png");
        assertEquals("png", avatarService.getFileExtension(file));

        when(file.getOriginalFilename()).thenReturn("archive");
        assertEquals("", avatarService.getFileExtension(file));

        when(file.getOriginalFilename()).thenReturn(null);
        assertEquals("", avatarService.getFileExtension(file));
    }

    @Test
    void testUploadAvatar_invalidFile() {
        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> avatarService.uploadAvatar(emptyFile, UUID.randomUUID()));

        MultipartFile nullFile = null;
        assertThrows(IllegalArgumentException.class,
                () -> avatarService.uploadAvatar(nullFile, UUID.randomUUID()));

        MultipartFile badType = mock(MultipartFile.class);
        when(badType.isEmpty()).thenReturn(false);
        when(badType.getContentType()).thenReturn("application/pdf");
        when(badType.getSize()).thenReturn(100L);

        assertThrows(IllegalArgumentException.class,
                () -> avatarService.uploadAvatar(badType, UUID.randomUUID()));

        MultipartFile tooBig = mock(MultipartFile.class);
        when(tooBig.isEmpty()).thenReturn(false);
        when(tooBig.getContentType()).thenReturn("image/png");
        when(tooBig.getSize()).thenReturn(6 * 1024 * 1024L); // > 5 Mo

        assertThrows(IllegalArgumentException.class,
                () -> avatarService.uploadAvatar(tooBig, UUID.randomUUID()));
    }

    @Test
    void testUploadAvatar_success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        UUID userId = UUID.randomUUID();
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn("avatar.png");
        when(file.getSize()).thenReturn(1024L);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[1024]));

        S3Client s3Client = mock(S3Client.class);
        S3ServiceClientConfiguration mockConfig = mock(S3ServiceClientConfiguration.class);
        Region mockRegion = mock(Region.class);

        when(s3Client.serviceClientConfiguration()).thenReturn(mockConfig);
        when(mockConfig.region()).thenReturn(mockRegion);
        when(mockRegion.id()).thenReturn("eu-west-1");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(mock(PutObjectResponse.class));

        AvatarService avatarService = new AvatarService(s3Client);

        String url = avatarService.uploadAvatar(file, userId);

        assertNotNull(url);
        assertTrue(url.contains("avatar.png"));
    }
}
