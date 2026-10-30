package com.smartclass.backend.dto;

import java.util.List;

public record VisionDetectionResponse(
        boolean detected,
        int personCount,
        List<PersonDetection> detections,
        long inferenceTimeMs
) {
}
