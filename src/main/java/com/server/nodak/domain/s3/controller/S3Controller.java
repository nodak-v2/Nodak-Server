package com.server.nodak.domain.s3.controller;

import com.server.nodak.domain.s3.dto.DeleteImageRequest;
import com.server.nodak.domain.s3.dto.UploadImageResponse;
import com.server.nodak.domain.s3.service.S3Service;
import com.server.nodak.domain.s3.validator.ImagePath;
import com.server.nodak.domain.s3.validator.ValidateImagePath;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/images")
    public ResponseEntity<UploadImageResponse> uploadImage(
            @RequestPart(value = "image") MultipartFile image,
            @RequestPart(value = "path") @ValidateImagePath(enumClass = ImagePath.class) String path
    ) throws IOException {
        UploadImageResponse result = s3Service.uploadImage(image, path);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/images")
    public ResponseEntity<Void> deleteImage(@RequestBody @Valid DeleteImageRequest imageRequest) {
        s3Service.deleteImage(imageRequest.getName());
        return ResponseEntity.ok().build();
    }
}
