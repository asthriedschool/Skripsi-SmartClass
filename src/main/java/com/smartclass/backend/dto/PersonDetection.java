package com.smartclass.backend.dto;

public record PersonDetection(
        double confidence,
        BoundingBox boundingBox
) {
}
