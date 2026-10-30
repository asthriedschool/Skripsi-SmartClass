package com.smartclass.backend.service;

import com.smartclass.backend.dto.BoundingBox;
import com.smartclass.backend.dto.PersonDetection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class YoloOutputDecoder {

    static final int PERSON_CLASS_INDEX = 0;
    static final int COCO_CLASS_COUNT = 80;
    static final int FEATURES_PER_DETECTION = COCO_CLASS_COUNT + 5;
    static final float DEFAULT_CONFIDENCE_THRESHOLD = 0.25f;
    static final float DEFAULT_IOU_THRESHOLD = 0.45f;

    private YoloOutputDecoder() {
    }

    static List<PersonDetection> decode(
            float[] output,
            int predictionCount,
            float confidenceThreshold,
            float iouThreshold,
            int imageWidth,
            int imageHeight,
            double scale,
            int padLeft,
            int padTop
    ) {
        if (output.length != predictionCount * FEATURES_PER_DETECTION) {
            throw new IllegalArgumentException(
                    "Output YOLO tidak sesuai jumlah fitur COCO yang diharapkan"
            );
        }

        List<Candidate> candidates = new ArrayList<>();
        for (int prediction = 0; prediction < predictionCount; prediction++) {
            int offset = prediction * FEATURES_PER_DETECTION;
            float objectness = output[offset + 4];
            float personProbability = output[offset + 5 + PERSON_CLASS_INDEX];
            float confidence = objectness * personProbability;
            float centerX = output[offset];
            float centerY = output[offset + 1];
            float width = output[offset + 2];
            float height = output[offset + 3];

            if (!Float.isFinite(confidence)
                    || confidence < confidenceThreshold
                    || !Float.isFinite(centerX)
                    || !Float.isFinite(centerY)
                    || !Float.isFinite(width)
                    || !Float.isFinite(height)
                    || width <= 0
                    || height <= 0) {
                continue;
            }

            double left = (centerX - width / 2.0 - padLeft) / scale;
            double top = (centerY - height / 2.0 - padTop) / scale;
            double right = (centerX + width / 2.0 - padLeft) / scale;
            double bottom = (centerY + height / 2.0 - padTop) / scale;

            left = clamp(left, 0, imageWidth);
            top = clamp(top, 0, imageHeight);
            right = clamp(right, 0, imageWidth);
            bottom = clamp(bottom, 0, imageHeight);

            if (right > left && bottom > top) {
                candidates.add(new Candidate(
                        confidence,
                        left,
                        top,
                        right,
                        bottom
                ));
            }
        }

        candidates.sort(Comparator.comparingDouble(Candidate::confidence).reversed());
        List<Candidate> selected = new ArrayList<>();
        for (Candidate candidate : candidates) {
            boolean suppressed = selected.stream()
                    .anyMatch(existing -> intersectionOverUnion(candidate, existing) > iouThreshold);
            if (!suppressed) {
                selected.add(candidate);
            }
        }

        return selected.stream()
                .map(candidate -> new PersonDetection(
                        candidate.confidence(),
                        new BoundingBox(
                                candidate.left(),
                                candidate.top(),
                                candidate.right() - candidate.left(),
                                candidate.bottom() - candidate.top()
                        )
                ))
                .toList();
    }

    private static double intersectionOverUnion(Candidate first, Candidate second) {
        double intersectionLeft = Math.max(first.left(), second.left());
        double intersectionTop = Math.max(first.top(), second.top());
        double intersectionRight = Math.min(first.right(), second.right());
        double intersectionBottom = Math.min(first.bottom(), second.bottom());
        double intersection = Math.max(0, intersectionRight - intersectionLeft)
                * Math.max(0, intersectionBottom - intersectionTop);
        double firstArea = (first.right() - first.left()) * (first.bottom() - first.top());
        double secondArea = (second.right() - second.left()) * (second.bottom() - second.top());
        return intersection / (firstArea + secondArea - intersection);
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private record Candidate(
            double confidence,
            double left,
            double top,
            double right,
            double bottom
    ) {
    }
}
