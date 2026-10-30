package com.smartclass.backend.service;

import com.smartclass.backend.dto.VisionDetectionResponse;
import com.smartclass.backend.exception.VisionInputException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class YoloDetectionServiceIntegrationTest {

    @Autowired
    private YoloDetectionService detectionService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loadsClasspathModelAndRunsInferenceOnImage() throws IOException {
        BufferedImage image = new BufferedImage(
                320,
                240,
                BufferedImage.TYPE_INT_RGB
        );
        image.setRGB(160, 120, Color.WHITE.getRGB());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bytes);

        VisionDetectionResponse result = detectionService.detect(bytes.toByteArray());

        assertNotNull(result);
        assertEquals(result.personCount(), result.detections().size());
        assertEquals(!result.detections().isEmpty(), result.detected());
        assertTrue(result.inferenceTimeMs() >= 0);
    }

    @Test
    void rejectsInvalidImageBytes() {
        assertThrows(
                VisionInputException.class,
                () -> detectionService.detect(new byte[]{1, 2, 3})
        );
    }

    @Test
    void endpointRequiresLoginAndAllowsAdminAndPetugas() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.png",
                "image/png",
                validPng()
        );

        mockMvc.perform(multipart("/api/vision/detect").file(file))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(multipart("/api/vision/detect")
                        .file(file)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detected").isBoolean())
                .andExpect(jsonPath("$.personCount").isNumber())
                .andExpect(jsonPath("$.detections").isArray())
                .andExpect(jsonPath("$.inferenceTimeMs").isNumber());

        mockMvc.perform(multipart("/api/vision/detect")
                        .file(file)
                        .with(user("petugas").roles("PETUGAS")))
                .andExpect(status().isOk());

        MockMultipartFile unsupported = new MockMultipartFile(
                "image",
                "test.txt",
                "text/plain",
                new byte[]{1, 2, 3}
        );
        mockMvc.perform(multipart("/api/vision/detect")
                        .file(unsupported)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());

        MockMultipartFile oversized = new MockMultipartFile(
                "image",
                "large.png",
                "image/png",
                new byte[10 * 1024 * 1024 + 1]
        );
        mockMvc.perform(multipart("/api/vision/detect")
                        .file(oversized)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isPayloadTooLarge());
    }

    private byte[] validPng() throws IOException {
        BufferedImage image = new BufferedImage(
                64,
                64,
                BufferedImage.TYPE_INT_RGB
        );
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bytes);
        return bytes.toByteArray();
    }
}
