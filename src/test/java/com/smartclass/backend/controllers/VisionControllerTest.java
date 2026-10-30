package com.smartclass.backend.controllers;

import com.smartclass.backend.exception.VisionInputException;
import com.smartclass.backend.exception.VisionPayloadTooLargeException;
import com.smartclass.backend.service.YoloDetectionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class VisionControllerTest {

    @Mock
    private YoloDetectionService detectionService;

    @Test
    void rejectsUnsupportedContentType() {
        VisionController controller = new VisionController(detectionService);
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "document.txt",
                "text/plain",
                new byte[]{1, 2, 3}
        );

        assertThrows(
                VisionInputException.class,
                () -> controller.detect(file)
        );
    }

    @Test
    void rejectsFilesOverTheConfiguredSize() {
        VisionController controller = new VisionController(detectionService);
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "large.png",
                "image/png",
                new byte[10 * 1024 * 1024 + 1]
        );

        assertThrows(
                VisionPayloadTooLargeException.class,
                () -> controller.detect(file)
        );
    }
}
