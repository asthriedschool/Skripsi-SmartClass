package com.smartclass.backend.service;

import com.smartclass.backend.exception.VisionInputException;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

@Component
class YoloImagePreprocessor {

    static final int INPUT_SIZE = 640;
    private static final int LETTERBOX_COLOR = 114;

    PreprocessedImage preprocess(BufferedImage source) {
        if (source == null || source.getWidth() <= 0 || source.getHeight() <= 0) {
            throw new VisionInputException("File bukan gambar yang valid");
        }

        double scale = Math.min(
                (double) INPUT_SIZE / source.getWidth(),
                (double) INPUT_SIZE / source.getHeight()
        );
        int resizedWidth = Math.max(1, (int) Math.round(source.getWidth() * scale));
        int resizedHeight = Math.max(1, (int) Math.round(source.getHeight() * scale));
        int padLeft = (INPUT_SIZE - resizedWidth) / 2;
        int padTop = (INPUT_SIZE - resizedHeight) / 2;

        BufferedImage letterboxed = new BufferedImage(
                INPUT_SIZE,
                INPUT_SIZE,
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D graphics = letterboxed.createGraphics();
        try {
            graphics.setColor(new Color(
                    LETTERBOX_COLOR,
                    LETTERBOX_COLOR,
                    LETTERBOX_COLOR
            ));
            graphics.fillRect(0, 0, INPUT_SIZE, INPUT_SIZE);
            graphics.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );
            graphics.drawImage(
                    source,
                    padLeft,
                    padTop,
                    resizedWidth,
                    resizedHeight,
                    null
            );
        } finally {
            graphics.dispose();
        }

        float[] tensor = new float[3 * INPUT_SIZE * INPUT_SIZE];
        int planeSize = INPUT_SIZE * INPUT_SIZE;
        for (int y = 0; y < INPUT_SIZE; y++) {
            for (int x = 0; x < INPUT_SIZE; x++) {
                int rgb = letterboxed.getRGB(x, y);
                int offset = y * INPUT_SIZE + x;
                tensor[offset] = ((rgb >> 16) & 0xFF) / 255.0f;
                tensor[planeSize + offset] = ((rgb >> 8) & 0xFF) / 255.0f;
                tensor[2 * planeSize + offset] = (rgb & 0xFF) / 255.0f;
            }
        }

        return new PreprocessedImage(tensor, scale, padLeft, padTop);
    }

    record PreprocessedImage(
            float[] tensor,
            double scale,
            int padLeft,
            int padTop
    ) {
    }
}
