package com.smartclass.backend.service;

import com.smartclass.backend.exception.VisionInputException;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class YoloImagePreprocessorTest {

    private final YoloImagePreprocessor preprocessor =
            new YoloImagePreprocessor();

    @Test
    void createsRgbNormalizedLetterboxedTensor() {
        BufferedImage image = new BufferedImage(
                320,
                640,
                BufferedImage.TYPE_INT_RGB
        );
        image.setRGB(0, 0, Color.RED.getRGB());
        image.setRGB(319, 639, Color.BLUE.getRGB());

        YoloImagePreprocessor.PreprocessedImage result =
                preprocessor.preprocess(image);

        int planeSize = 640 * 640;
        assertEquals(3 * planeSize, result.tensor().length);
        assertEquals(1.0f, result.tensor()[160], 0.01f);
        assertEquals(0.0f, result.tensor()[planeSize + 160], 0.01f);
        assertEquals(114 / 255.0f, result.tensor()[0], 0.01f);
        assertEquals(160, result.padLeft());
        assertEquals(0, result.padTop());
    }

    @Test
    void rejectsMissingImage() {
        assertThrows(
                VisionInputException.class,
                () -> preprocessor.preprocess(null)
        );
    }
}
