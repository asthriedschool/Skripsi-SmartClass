package com.smartclass.backend.service;

import com.smartclass.backend.dto.PersonDetection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class YoloOutputDecoderTest {

    @Test
    void selectsPersonAppliesNmsAndMapsBoxToOriginalImage() {
        float[] output = new float[2 * YoloOutputDecoder.FEATURES_PER_DETECTION];
        setPrediction(output, 0, 320, 320, 200, 200, 0.9f, 0.9f);
        setPrediction(output, 1, 330, 320, 200, 200, 0.8f, 0.9f);

        List<PersonDetection> detections = YoloOutputDecoder.decode(
                output,
                2,
                YoloOutputDecoder.DEFAULT_CONFIDENCE_THRESHOLD,
                YoloOutputDecoder.DEFAULT_IOU_THRESHOLD,
                320,
                320,
                2.0,
                0,
                0
        );

        assertEquals(1, detections.size());
        assertEquals(0.81, detections.get(0).confidence(), 0.001);
        assertEquals(110, detections.get(0).boundingBox().x(), 0.01);
        assertEquals(110, detections.get(0).boundingBox().y(), 0.01);
        assertEquals(100, detections.get(0).boundingBox().width(), 0.01);
        assertEquals(100, detections.get(0).boundingBox().height(), 0.01);
    }

    @Test
    void filtersLowConfidenceAndNonPersonClasses() {
        float[] output = new float[2 * YoloOutputDecoder.FEATURES_PER_DETECTION];
        setPrediction(output, 0, 320, 320, 100, 100, 0.1f, 0.99f);
        int secondOffset = YoloOutputDecoder.FEATURES_PER_DETECTION;
        output[secondOffset] = 320;
        output[secondOffset + 1] = 320;
        output[secondOffset + 2] = 100;
        output[secondOffset + 3] = 100;
        output[secondOffset + 4] = 0.99f;
        output[secondOffset + 6] = 0.99f;

        List<PersonDetection> detections = YoloOutputDecoder.decode(
                output,
                2,
                YoloOutputDecoder.DEFAULT_CONFIDENCE_THRESHOLD,
                YoloOutputDecoder.DEFAULT_IOU_THRESHOLD,
                640,
                640,
                1.0,
                0,
                0
        );

        assertEquals(0, detections.size());
    }

    private void setPrediction(
            float[] output,
            int prediction,
            float centerX,
            float centerY,
            float width,
            float height,
            float objectness,
            float personProbability
    ) {
        int offset = prediction * YoloOutputDecoder.FEATURES_PER_DETECTION;
        output[offset] = centerX;
        output[offset + 1] = centerY;
        output[offset + 2] = width;
        output[offset + 3] = height;
        output[offset + 4] = objectness;
        output[offset + 5] = personProbability;
    }
}
