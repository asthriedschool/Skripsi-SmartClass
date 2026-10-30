package com.smartclass.backend.controllers;

import com.smartclass.backend.dto.VisionDetectionResponse;
import com.smartclass.backend.exception.VisionInputException;
import com.smartclass.backend.exception.VisionInferenceException;
import com.smartclass.backend.exception.VisionPayloadTooLargeException;
import com.smartclass.backend.service.YoloDetectionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/vision")
public class VisionController {

    private static final long MAX_IMAGE_SIZE_BYTES = 10L * 1024 * 1024;
    private static final Set<String> ALLOWED_IMAGE_TYPES =
            Set.of(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE);

    private final YoloDetectionService detectionService;

    public VisionController(YoloDetectionService detectionService) {
        this.detectionService = detectionService;
    }

    @PostMapping(
            path = "/detect",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public VisionDetectionResponse detect(
            @RequestPart("image") MultipartFile image
    ) {
        if (image.isEmpty()) {
            throw new VisionInputException("File gambar tidak boleh kosong");
        }
        if (image.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new VisionPayloadTooLargeException(
                    "Ukuran gambar tidak boleh lebih dari 10 MB"
            );
        }
        if (image.getContentType() == null
                || !ALLOWED_IMAGE_TYPES.contains(image.getContentType())) {
            throw new VisionInputException(
                    "Tipe gambar harus image/jpeg atau image/png"
            );
        }

        try {
            return detectionService.detect(image.getBytes());
        } catch (IOException e) {
            throw new VisionInferenceException(
                    "Gagal membaca file gambar yang diunggah",
                    e
            );
        }
    }
}
