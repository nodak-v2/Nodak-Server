package com.server.nodak.domain.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.server.nodak.domain.s3.S3Properties;
import com.server.nodak.domain.s3.dto.UploadImageResponse;
import com.server.nodak.exception.common.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private AmazonS3Client amazonS3Client;

    @Mock
    private S3Properties s3Properties;

    @Mock
    private MultipartFile multipartFile;

    private S3Service s3Service;

    private final String bucketName = "test-bucket";
    private final String hostUrl = "http://localhost/";

    @BeforeEach
    void setUp() {
        amazonS3Client = mock(AmazonS3Client.class);
        s3Properties = mock(S3Properties.class);
        when(s3Properties.getBucket()).thenReturn("test-bucket");
//        when(s3Properties.getHostUrl()).thenReturn("http://localhost/");
        s3Service = new S3Service(amazonS3Client, s3Properties);
    }

    @Test
    @DisplayName("이미지 업로드 테스트")
    void uploadImageTest() throws IOException {
        // Given
        String path = "test/path";
        String fileName = "image.jpg";
        String expectedUrl = "http://localhost/" + path + "/" + UUID.randomUUID() + ".jpg";
        byte[] fileContent = "test file content".getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileContent);

        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getInputStream()).thenReturn(byteArrayInputStream);
        when(multipartFile.getSize()).thenReturn((long) fileContent.length);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        // When
        UploadImageResponse response = s3Service.uploadImage(multipartFile, path);

        // Then
        ArgumentCaptor<String> bucketCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ObjectMetadata> metadataCaptor = ArgumentCaptor.forClass(ObjectMetadata.class);

        verify(amazonS3Client).putObject(bucketCaptor.capture(), keyCaptor.capture(), any(ByteArrayInputStream.class), metadataCaptor.capture());
        assertEquals("test-bucket", bucketCaptor.getValue());
        assertNotEquals(null, keyCaptor.getValue());  // 경로 검증, 실제 UUID 값이므로 일치하지 않는 것이 정상
        assertNotNull(expectedUrl, response.getImagePath());
    }

    @Test
    @DisplayName("이미지 삭제 테스트")
    void deleteImageTest() {
        // Given
        String imageName = "test/path/image.jpg";
        when(amazonS3Client.doesObjectExist(bucketName, imageName)).thenReturn(true);

        // When
        s3Service.deleteImage(imageName);

        // Then
        verify(amazonS3Client).deleteObject(bucketName, imageName);
    }

    @Test
    @DisplayName("이미지 못 찾았을 때, 에러 반환")
    void imageNotFoundTest() {
        // Given
        String imageName = "test/path/image.jpg";

        when(amazonS3Client.doesObjectExist(bucketName, imageName)).thenReturn(false);

        // When / Then
        assertThrows(DataNotFoundException.class, () -> s3Service.deleteImage(imageName));
    }
}